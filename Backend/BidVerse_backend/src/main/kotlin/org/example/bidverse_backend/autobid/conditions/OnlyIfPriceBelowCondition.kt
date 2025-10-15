package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * only_if_price_below: Only if current price is less than...
 * Type: number
 */
@Component
class OnlyIfPriceBelowCondition : ConditionHandler {
    override val conditionName = "only_if_price_below"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val maxPrice = when (conditionValue) {
            is Number -> java.math.BigDecimal(conditionValue.toString())
            else -> return true
        }

        return context.currentPrice < maxPrice
    }
}
