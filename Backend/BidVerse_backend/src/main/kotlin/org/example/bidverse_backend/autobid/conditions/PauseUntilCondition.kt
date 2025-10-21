package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * pause_until: Pause for a while (e.g. at night)
 * Type: string (ISO datetime)
 * Example: "2025-10-06T07:00:00"
 */
@Component
class PauseUntilCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(PauseUntilCondition::class.java)
    override val conditionName = "pause_until"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val pauseUntil = when (conditionValue) {
            is String -> try {
                LocalDateTime.parse(conditionValue)
            } catch (e: Exception) {
                logger.warn("    [pause_until] Invalid datetime format: $conditionValue")
                return true // Invalid format, ignore condition
            }
            else -> return true
        }

        // Only bid if current time is after the pause time
        val canBid = context.currentTime.isAfter(pauseUntil)
        
        logger.info("    [pause_until] Current: ${context.currentTime}, Pause until: $pauseUntil → $canBid")
        
        return canBid
    }
}
