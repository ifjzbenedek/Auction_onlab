package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * near_end_minutes: Activate if less than X minutes remaining
 * Type: number
 */
@Component
class NearEndMinutesCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(NearEndMinutesCondition::class.java)
    override val conditionName = "near_end_minutes"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val thresholdMinutes = when (conditionValue) {
            is Number -> conditionValue.toLong()
            else -> return true
        }

        // Only bid if we're within the threshold
        val minutesLeft = context.getMinutesUntilEnd()
        val canBid = minutesLeft <= thresholdMinutes
        
        logger.info("    [near_end_minutes] Minutes left: $minutesLeft, Threshold: $thresholdMinutes → $canBid")
        
        return canBid
    }
}
