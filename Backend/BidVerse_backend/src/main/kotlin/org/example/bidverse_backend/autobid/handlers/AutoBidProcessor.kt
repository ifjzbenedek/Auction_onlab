package org.example.bidverse_backend.autobid.handlers

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.autobid.AutoBidDecision
import org.example.bidverse_backend.autobid.conditions.ConditionHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Processes all conditions and decides whether to bid and how much.
 */
@Service
class AutoBidProcessor(
    private val conditionHandlers: List<ConditionHandler>
) {
    private val logger = LoggerFactory.getLogger(AutoBidProcessor::class.java)
    
    /**
     * Process autobid and return decision
     */
    fun processAutoBid(context: AutoBidContext): AutoBidDecision {
        logger.info("=== Processing AutoBid ${context.autoBid.id} for Auction ${context.auction.id} ===")
        
        // Basic validations
        if (context.isAuctionEnded()) {
            logger.warn("AutoBid ${context.autoBid.id}: Auction has ended")
            return AutoBidDecision.StopAutoBid("Auction has ended")
        }

        if (!context.autoBid.isActive) {
            logger.warn("AutoBid ${context.autoBid.id}: Not active")
            return AutoBidDecision.SkipBid("AutoBid is not active")
        }

        // Check if user is already winning
        if (context.isUserWinning()) {
            logger.info("AutoBid ${context.autoBid.id}: User is already winning")
            return AutoBidDecision.SkipBid("User is already the highest bidder")
        }

        val conditions = context.autoBid.conditionsJson
        if (conditions == null) {
            logger.warn("AutoBid ${context.autoBid.id}: No conditions configured")
            return AutoBidDecision.SkipBid("No conditions configured")
        }
        
        logger.info("AutoBid ${context.autoBid.id}: Configured conditions: ${conditions.keys}")

        // Check all "shouldBid" conditions
        for (handler in conditionHandlers) {
            val conditionValue = conditions[handler.conditionName]
            if (conditionValue != null) {
                val shouldBid = handler.shouldBid(context, conditionValue)
                logger.info("AutoBid ${context.autoBid.id}: Condition '${handler.conditionName}' = $conditionValue -> shouldBid = $shouldBid")
                if (!shouldBid) {
                    logger.warn("AutoBid ${context.autoBid.id}: Condition '${handler.conditionName}' not met, skipping")
                    return AutoBidDecision.SkipBid("Condition '${handler.conditionName}' not met")
                }
            }
        }

        // Calculate bid amount
        var bidAmount = calculateBidAmount(context, conditions)
        var reason = "All conditions met"
        logger.info("AutoBid ${context.autoBid.id}: Calculated bid amount: $bidAmount")

        // Check if bid exceeds max - if so, cap it at max
        val maxBid = context.autoBid.maxBidAmount
        if (maxBid != null && bidAmount > maxBid) {
            logger.info("AutoBid ${context.autoBid.id}: Bid $bidAmount exceeds max $maxBid, capping")
            bidAmount = maxBid
            reason = "Bid capped at maximum bid amount ($maxBid)"
        }

        // Check if bid is higher than current price
        val currentPrice = context.getCurrentPrice()
        logger.info("AutoBid ${context.autoBid.id}: Current price: $currentPrice, Bid amount: $bidAmount")
        if (bidAmount.compareTo(currentPrice) <= 0) {
            logger.warn("AutoBid ${context.autoBid.id}: Calculated bid ($bidAmount) is not higher than current price ($currentPrice)")
            return AutoBidDecision.SkipBid("Calculated bid ($bidAmount) is not higher than current price ($currentPrice)")
        }

        logger.info("AutoBid ${context.autoBid.id}: Placing bid: $bidAmount - $reason")
        return AutoBidDecision.PlaceBid(
            amount = bidAmount,
            reason = reason
        )
    }

    /**
     * Calculate the bid amount based on base increment and conditions
     */
    private fun calculateBidAmount(context: AutoBidContext, conditions: Map<String, Any>): BigDecimal {
        val currentPrice = context.getCurrentPrice()
        val increment = context.autoBid.incrementAmount ?: return currentPrice
        
        logger.info("Current price: $currentPrice, Increment: $increment")

        // If user hasn't bid yet and startingBidAmount is set, use that
        val hasUserBid = context.lastBidByThisAutoBid != null
        logger.info("User has bid before: $hasUserBid")
        
        if (!hasUserBid && context.autoBid.startingBidAmount != null) {
            val startingBid = context.autoBid.startingBidAmount!!
            logger.info("Starting bid amount configured: $startingBid")
            // Only use starting bid if it's higher than current price
            if (startingBid.compareTo(currentPrice) > 0) {
                logger.info(" Using starting bid: $startingBid (higher than current price)")
                return startingBid
            } else {
                logger.info("Starting bid $startingBid is not higher than current price $currentPrice, using increment")
            }
        }

        // Otherwise, start with current price + increment
        var bidAmount = currentPrice.add(increment)
        logger.info("Base bid amount: $bidAmount (currentPrice + increment)")

        // Apply all condition modifiers in order
        for (handler in conditionHandlers) {
            val conditionValue = conditions[handler.conditionName]
            if (conditionValue != null) {
                val originalAmount = bidAmount
                val modifiedAmount = handler.modifyBidAmount(context, conditionValue, bidAmount)
                if (modifiedAmount != null) {
                    bidAmount = modifiedAmount
                    logger.info("Modifier '${handler.conditionName}' changed bid: $originalAmount → $bidAmount")
                }
            }
        }

        logger.info("Final calculated bid amount: $bidAmount")
        return bidAmount
    }
}
