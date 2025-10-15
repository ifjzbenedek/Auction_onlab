package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * last_minute_rush: If less than 1 minute left, bid more aggressively
 * Type: boolean
 */
@Component
class LastMinuteRushCondition : ConditionHandler {
    override val conditionName = "last_minute_rush"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue != true) return null

        // If less than 1 minute, increase by 50%
        if (context.minutesUntilEnd < 1) {
            val increment = baseAmount - context.currentPrice
            val aggressiveIncrement = increment.multiply(BigDecimal("1.5"))
            return context.currentPrice + aggressiveIncrement
        }

        return null
    }
}
