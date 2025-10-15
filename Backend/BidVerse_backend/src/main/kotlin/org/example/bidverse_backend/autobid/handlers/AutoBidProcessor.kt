package org.example.bidverse_backend.autobid.handlers

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.autobid.AutoBidDecision
import org.example.bidverse_backend.autobid.conditions.ConditionHandler
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Processes all conditions and decides whether to bid and how much.
 */
@Service
class AutoBidProcessor(
    private val conditionHandlers: List<ConditionHandler>
) {
    
    /**
     * Process autobid and return decision
     */
    fun processAutoBid(context: AutoBidContext): AutoBidDecision {
        // Basic validations
        if (context.isAuctionEnded()) {
            return AutoBidDecision.StopAutoBid("Auction has ended")
        }

        if (!context.autoBid.isActive) {
            return AutoBidDecision.SkipBid("AutoBid is not active")
        }

        // Check if user is already winning
        if (context.isUserWinning()) {
            return AutoBidDecision.SkipBid("User is already the highest bidder")
        }

        val conditions = context.autoBid.conditionsJson
        if (conditions == null) {
            return AutoBidDecision.SkipBid("No conditions configured")
        }

        // Check all "shouldBid" conditions
        for (handler in conditionHandlers) {
            val conditionValue = conditions[handler.conditionName]
            if (conditionValue != null) {
                if (!handler.shouldBid(context, conditionValue)) {
                    return AutoBidDecision.SkipBid("Condition '${handler.conditionName}' not met")
                }
            }
        }

        // Calculate bid amount
        var bidAmount = calculateBidAmount(context, conditions)
        var reason = "All conditions met"

        // Check if bid exceeds max - if so, cap it at max
        val maxBid = context.autoBid.maxBidAmount
        if (maxBid != null && bidAmount.compareTo(maxBid) > 0) {
            bidAmount = maxBid
            reason = "Bid capped at maximum bid amount ($maxBid)"
        }

        // Check if bid is higher than current price
        val currentPrice = context.getCurrentPrice()
        if (bidAmount.compareTo(currentPrice) <= 0) {
            return AutoBidDecision.SkipBid("Calculated bid ($bidAmount) is not higher than current price ($currentPrice)")
        }

        return AutoBidDecision.PlaceBid(
            amount = bidAmount,
            reason = reason
        )
    }

    /**
     * Calculate the bid amount based on base increment and conditions
     */
    private fun calculateBidAmount(context: AutoBidContext, conditions: Map<String, Any>): BigDecimal {
        // Start with current price + increment
        val currentPrice = context.getCurrentPrice()
        val increment = context.autoBid.incrementAmount
        if (increment == null) {
            return currentPrice
        }
        
        var bidAmount = currentPrice.add(increment)

        // Apply all condition modifiers in order
        for (handler in conditionHandlers) {
            val conditionValue = conditions[handler.conditionName]
            if (conditionValue != null) {
                val modifiedAmount = handler.modifyBidAmount(context, conditionValue, bidAmount)
                if (modifiedAmount != null) {
                    bidAmount = modifiedAmount
                }
            }
        }

        return bidAmount
    }
}
