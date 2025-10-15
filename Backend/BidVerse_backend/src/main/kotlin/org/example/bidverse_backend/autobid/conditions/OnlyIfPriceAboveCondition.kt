package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * only_if_price_above: Only if reached a minimum
 * Type: number
 */
@Component
class OnlyIfPriceAboveCondition : ConditionHandler {
    override val conditionName = "only_if_price_above"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val minPrice = when (conditionValue) {
            is Number -> java.math.BigDecimal(conditionValue.toString())
            else -> return true
        }

        return context.currentPrice >= minPrice
    }
}
