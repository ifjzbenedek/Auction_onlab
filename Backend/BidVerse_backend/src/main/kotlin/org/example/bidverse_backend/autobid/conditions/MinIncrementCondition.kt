package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * min_increment: Minimum increment if other conditions don't specify
 * Type: number
 */
@Component
class MinIncrementCondition : ConditionHandler {
    override val conditionName = "min_increment"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue == null) return null

        val minIncrement = when (conditionValue) {
            is Number -> BigDecimal(conditionValue.toString())
            else -> return null
        }

        val currentPrice = context.getCurrentPrice()
        val newAmount = currentPrice.add(minIncrement)
        if (newAmount > baseAmount) {
            return baseAmount
        }
        return newAmount
    }
}
