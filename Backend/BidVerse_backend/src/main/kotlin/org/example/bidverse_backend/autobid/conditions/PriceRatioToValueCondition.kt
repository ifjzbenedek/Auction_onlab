package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * price_ratio_to_value: Don't bid if price exceeds minBid * X
 * Type: number
 * Example: 1.5 means don't bid if current price > startingPrice * 1.5
 */
@Component
class PriceRatioToValueCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(PriceRatioToValueCondition::class.java)
    override val conditionName = "price_ratio_to_value"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val ratio = when (conditionValue) {
            is Number -> BigDecimal(conditionValue.toString())
            else -> return true
        }

        val minPrice = context.auction.minimumPrice
        val maxAllowedPrice = minPrice.multiply(ratio)
        val currentPrice = context.getCurrentPrice()
        
        val canBid = currentPrice <= maxAllowedPrice
        
        logger.info("    [price_ratio_to_value] Current: $currentPrice, Max allowed: $maxAllowedPrice (${minPrice} × ${ratio}) → $canBid")
        
        return canBid
    }
}
