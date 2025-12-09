package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * last_minute_rush: If less than 1 minute left, bid more aggressively
 * Type: boolean
 */
@Component
class LastMinuteRushCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(LastMinuteRushCondition::class.java)
    override val conditionName = "last_minute_rush"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue != true) return null

        val minutesLeft = context.getMinutesUntilEnd()
        
        // If less than 1 minute, increase the entire bid amount by 25%
        if (minutesLeft <= 1) {
            val currentPrice = context.getCurrentPrice()
            val newAmount = currentPrice.multiply(BigDecimal("1.25"))
            
            logger.info("    [last_minute_rush] ${minutesLeft}min left, increasing current price by 25%: $currentPrice → $newAmount")
            
            return newAmount
        }

        logger.debug("    [last_minute_rush] ${minutesLeft}min left, not in rush mode yet")
        return null
    }
}
