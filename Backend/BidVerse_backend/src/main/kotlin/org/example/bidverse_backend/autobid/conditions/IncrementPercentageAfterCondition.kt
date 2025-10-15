package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * increment_percentage_after: Percentage increase after certain price (like step_after but in %)
 * Type: object - map of price threshold to percentage (0.05 = 5%)
 * Example: {"20000": 0.05, "50000": 0.10}

@Component
class IncrementPercentageAfterCondition : ConditionHandler {
    override val conditionName = "increment_percentage_after"

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

        // Sort thresholds descending
        val sortedThresholds = thresholds.entries
            .mapNotNull { entry ->
                val threshold = entry.key.toBigDecimalOrNull() ?: return@mapNotNull null
                val percentage = when (val value = entry.value) {
                    is Number -> BigDecimal(value.toString())
                    else -> return@mapNotNull null
                }
                threshold to percentage
            }
            .sortedByDescending { it.first }

        // Find the first threshold that the current price exceeds
        for ((threshold, percentage) in sortedThresholds) {
            if (context.currentPrice >= threshold) {
                val increment = context.currentPrice.multiply(percentage)
                return context.currentPrice.add(increment).setScale(0, RoundingMode.HALF_UP)
            }
        }

        return null
    }
}
*/