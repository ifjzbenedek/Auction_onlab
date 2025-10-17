package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * increment_percentage_after: Percentage increase after certain price (like step_after but in %)
 * Type: object - map of price threshold to percentage (0.05 = 5%)
 * Example: {"20000": 0.05, "50000": 0.10}
*/
@Component
class IncrementPercentageAfterCondition : ConditionHandler {
    override val conditionName = "increment_percentage_after"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        val thresholds = (conditionValue as? Map<*, *>) ?: return null
        
        val currentPrice = context.getCurrentPrice()

        val matchingPercentage = thresholds.entries
            .mapNotNull { (key, value) -> parseThresholdEntry(key, value) }
            .sortedByDescending { it.first }
            .firstOrNull { (threshold, _) -> currentPrice >= threshold }
            ?.second
            ?: return null

        val increment = currentPrice.multiply(matchingPercentage)
        return currentPrice.add(increment).setScale(0, RoundingMode.HALF_UP)
    }

    private fun parseThresholdEntry(key: Any?, value: Any?): Pair<BigDecimal, BigDecimal>? {
        val threshold = (key as? String)?.toBigDecimalOrNull() ?: return null
        val percentage = (value as? Number)?.let { BigDecimal(it.toString()) } ?: return null
        return threshold to percentage
    }
}