package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * increment_step_after: Bid with different steps after certain price
 * Type: object - map of price threshold to increment amount
 * Example: {"20000": 1000, "50000": 5000}
 */
@Component
class IncrementStepAfterCondition : ConditionHandler {
    override val conditionName = "increment_step_after"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    @Suppress("UNCHECKED_CAST")
    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue == null) return null

        val thresholds = when (conditionValue) {
            is Map<*, *> -> conditionValue as Map<String, Any>
            else -> return null
        }

        // Sort thresholds descending to find the highest applicable one
        val sortedThresholds = thresholds.entries
            .mapNotNull { entry ->
                val threshold = entry.key.toBigDecimalOrNull() ?: return@mapNotNull null
                val increment = when (val value = entry.value) {
                    is Number -> BigDecimal(value.toString())
                    else -> return@mapNotNull null
                }
                threshold to increment
            }
            .sortedByDescending { it.first }

        // Find the first threshold that the current price exceeds
        for ((threshold, increment) in sortedThresholds) {
            if (context.currentPrice >= threshold) {
                return context.currentPrice + increment
            }
        }

        return null // No applicable threshold
    }
}
