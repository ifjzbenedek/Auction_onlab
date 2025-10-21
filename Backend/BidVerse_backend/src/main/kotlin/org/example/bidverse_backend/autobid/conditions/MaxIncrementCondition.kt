package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * max_increment: Never increase more than this
 * Type: number
 */
@Component
class MaxIncrementCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(MaxIncrementCondition::class.java)
    override val conditionName = "max_increment"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue == null) return null

        val maxIncrement = when (conditionValue) {
            is Number -> BigDecimal(conditionValue.toString())
            else -> return null
        }

        val currentPrice = context.getCurrentPrice()
        val increment = baseAmount - currentPrice
        
        if (increment > maxIncrement) {
            val cappedAmount = currentPrice + maxIncrement
            logger.info("    [max_increment] Capping increment: $increment > $maxIncrement, bid $baseAmount → $cappedAmount")
            return cappedAmount
        }

        logger.debug("    [max_increment] Increment $increment <= max $maxIncrement, OK")
        return null
    }
}
