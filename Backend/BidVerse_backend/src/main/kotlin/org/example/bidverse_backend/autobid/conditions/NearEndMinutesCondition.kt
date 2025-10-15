package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * near_end_minutes: Activate if less than X minutes remaining
 * Type: number
 */
@Component
class NearEndMinutesCondition : ConditionHandler {
    override val conditionName = "near_end_minutes"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val thresholdMinutes = when (conditionValue) {
            is Number -> conditionValue.toLong()
            else -> return true
        }

        // Only bid if we're within the threshold
        val minutesLeft = context.getMinutesUntilEnd()
        return minutesLeft <= thresholdMinutes
    }
}
