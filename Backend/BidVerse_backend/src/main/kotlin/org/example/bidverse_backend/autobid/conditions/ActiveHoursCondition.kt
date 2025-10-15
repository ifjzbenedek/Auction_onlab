package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * active_hours: Only work during certain hours (list of hours)
 * Type: array of integers (0-23)
 */
@Component
class ActiveHoursCondition : ConditionHandler {
    override val conditionName = "active_hours"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val activeHours = when (conditionValue) {
            is List<*> -> conditionValue.mapNotNull { 
                when (it) {
                    is Number -> it.toInt()
                    else -> null
                }
            }
            else -> return true
        }

        val currentHour = context.currentTime.hour
        return currentHour in activeHours
    }
}
