package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * active_hours: Only work during certain hours (list of hours)
 * Type: array of integers (0-23)
 */
@Component
class ActiveHoursCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(ActiveHoursCondition::class.java)
    override val conditionName = "active_hours"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        val activeHours = (conditionValue as? List<*>)
            ?.mapNotNull { (it as? Number)?.toInt() }
            ?: run {
                logger.debug("    [active_hours] No active hours configured, allowing bid")
                return true
            }

        val currentHour = context.currentTime.hour
        val isActive = currentHour in activeHours
        
        logger.info("    [active_hours] Current hour: $currentHour, Active hours: $activeHours → $isActive")
        
        return isActive
    }
}
