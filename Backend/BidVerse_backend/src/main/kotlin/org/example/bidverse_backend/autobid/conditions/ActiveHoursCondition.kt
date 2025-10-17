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
        val activeHours = (conditionValue as? List<*>)
            ?.mapNotNull { (it as? Number)?.toInt() }
            ?: return true

        return context.currentTime.hour in activeHours
    }
}
