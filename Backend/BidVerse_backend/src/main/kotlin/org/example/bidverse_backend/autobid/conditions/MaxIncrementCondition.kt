package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * max_increment: Never increase more than this
 * Type: number
 */
@Component
class MaxIncrementCondition : ConditionHandler {
    override val conditionName = "max_increment"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue == null) return null

        val maxIncrement = when (conditionValue) {
            is Number -> BigDecimal(conditionValue.toString())
            else -> return null
        }

        val increment = baseAmount - context.getCurrentPrice()
        if (increment > maxIncrement) {
            return context.getCurrentPrice() + maxIncrement
        }

        return null
    }
}
