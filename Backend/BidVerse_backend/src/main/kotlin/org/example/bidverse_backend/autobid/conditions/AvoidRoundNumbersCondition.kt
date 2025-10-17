package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * avoid_round_numbers: Don't bid on round numbers (e.g. 10,000)
 * Type: boolean
 */
@Component
class AvoidRoundNumbersCondition : ConditionHandler {
    override val conditionName = "avoid_round_numbers"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue != true) return null

        val roundedAmount = baseAmount.setScale(0, RoundingMode.HALF_UP)
        
        if (roundedAmount.remainder(BigDecimal(100)) != BigDecimal.ZERO) return null

        val oddOffset = listOf(7, 11, 13, 17, 23, 29, 37, 47).random()
        return roundedAmount.add(BigDecimal(oddOffset))
    }
}
