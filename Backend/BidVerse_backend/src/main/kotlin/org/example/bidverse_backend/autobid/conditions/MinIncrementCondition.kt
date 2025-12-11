package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * min_increment: Minimum increment if other conditions don't specify
 * Type: number
 */
@Component
class MinIncrementCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(MinIncrementCondition::class.java)
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
            logger.info("    [min_increment] Enforcing minimum: $baseAmount < min ($currentPrice + $minIncrement = $newAmount)")
            return newAmount
        }
        
        logger.info("    [min_increment] Base amount $baseAmount already >= min ($newAmount), no change needed")
        return null
    }
}
