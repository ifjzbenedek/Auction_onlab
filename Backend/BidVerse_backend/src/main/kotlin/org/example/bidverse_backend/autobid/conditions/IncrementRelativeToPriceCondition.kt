package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * increment_relative_to_price: Increase as percentage of current price (e.g. 0.05 = +5%)
 * Type: number (percentage as decimal)
 */
@Component
class IncrementRelativeToPriceCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(IncrementRelativeToPriceCondition::class.java)
    override val conditionName = "increment_relative_to_price"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue == null) return null

        val percentage = when (conditionValue) {
            is Number -> BigDecimal(conditionValue.toString())
            else -> return null
        }

        val currentPrice = context.getCurrentPrice()
        val increment = currentPrice.multiply(percentage)
        val newAmount = currentPrice.add(increment).setScale(0, RoundingMode.HALF_UP)
        
        logger.info("    [increment_relative_to_price] ${(percentage * BigDecimal(100))}% of $currentPrice = increment $increment → $newAmount")
        
        return newAmount
    }
}
