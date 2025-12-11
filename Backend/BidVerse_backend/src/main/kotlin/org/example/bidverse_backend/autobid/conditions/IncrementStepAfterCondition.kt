package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * increment_step_after: Bid with different steps after certain price
 * Type: object - map of price threshold to increment amount
 * Example: {"20000": 1000, "50000": 5000}
 */
@Component
class IncrementStepAfterCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(IncrementStepAfterCondition::class.java)
    override val conditionName = "increment_step_after"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        val thresholds = (conditionValue as? Map<*, *>) ?: return null
        
        val currentPrice = context.getCurrentPrice()

        val matchingIncrement = thresholds.entries
            .mapNotNull { (key, value) -> parseThresholdEntry(key, value) }
            .sortedByDescending { it.first }
            .firstOrNull { (threshold, _) -> currentPrice >= threshold }
            ?.second

        if (matchingIncrement == null) {
            logger.debug("    [increment_step_after] No matching threshold for price $currentPrice")
            return null
        }

        val newAmount = currentPrice.add(matchingIncrement)
        
        logger.info("    [increment_step_after] Price $currentPrice → increment $matchingIncrement → $newAmount")
        
        return newAmount
    }

    private fun parseThresholdEntry(key: Any?, value: Any?): Pair<BigDecimal, BigDecimal>? {
        val threshold = (key as? String)?.toBigDecimalOrNull() ?: return null
        val increment = (value as? Number)?.let { BigDecimal(it.toString()) } ?: return null
        return threshold to increment
    }
}
