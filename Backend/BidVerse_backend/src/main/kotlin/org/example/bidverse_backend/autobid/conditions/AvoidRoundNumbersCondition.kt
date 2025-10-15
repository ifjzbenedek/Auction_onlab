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

        val amount = baseAmount.setScale(0, RoundingMode.HALF_UP)
        
        // Check if it's a round number (divisible by 1000, 500, or 100)
        val isRound = when {
            amount.remainder(BigDecimal(1000)) == BigDecimal.ZERO -> true
            amount.remainder(BigDecimal(500)) == BigDecimal.ZERO -> true
            amount.remainder(BigDecimal(100)) == BigDecimal.ZERO -> true
            else -> false
        }

        if (isRound) {
            // Add a small odd number (e.g., 7, 13, 23)
            val oddNumbers = listOf(7, 13, 23, 37, 47)
            val offset = oddNumbers.random()
            return amount.add(BigDecimal(offset))
        }

        return null
    }
}
