package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * price_ratio_to_value: Don't bid if price exceeds minBid * X
 * Type: number
 * Example: 1.5 means don't bid if current price > startingPrice * 1.5
 */
@Component
class PriceRatioToValueCondition : ConditionHandler {
    override val conditionName = "price_ratio_to_value"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val ratio = when (conditionValue) {
            is Number -> BigDecimal(conditionValue.toString())
            else -> return true
        }

        val maxAllowedPrice = context.auction.minimumPrice.multiply(ratio)
        
        // Only bid if current price hasn't exceeded the ratio
        return context.getCurrentPrice() <= maxAllowedPrice
    }
}
