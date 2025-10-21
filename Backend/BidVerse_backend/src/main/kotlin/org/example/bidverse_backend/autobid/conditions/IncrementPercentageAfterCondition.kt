package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(IncrementPercentageAfterCondition::class.java)
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

        if (matchingPercentage == null) {
            logger.debug("    [increment_percentage_after] No matching threshold for price $currentPrice")
            return null
        }

        val increment = currentPrice.multiply(matchingPercentage)
        val newAmount = currentPrice.add(increment).setScale(0, RoundingMode.HALF_UP)
        
        logger.info("    [increment_percentage_after] Price $currentPrice → ${(matchingPercentage * BigDecimal(100))}% increment → $newAmount")
        
        return newAmount
    }

    private fun parseThresholdEntry(key: Any?, value: Any?): Pair<BigDecimal, BigDecimal>? {
        val threshold = (key as? String)?.toBigDecimalOrNull() ?: return null
        val percentage = (value as? Number)?.let { BigDecimal(it.toString()) } ?: return null
        return threshold to percentage
    }
}