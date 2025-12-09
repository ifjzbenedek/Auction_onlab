package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * if_no_activity_for_dd_hh_mm: If no new bid for dd days hh hours mm minutes, increase
 * Type: string
 * Format: "dd_hh_mm" (e.g., "2_3_30" means 2 days, 3 hours, 30 minutes)
*/
@Component
class IfNoActivityForDdHhMmCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(IfNoActivityForDdHhMmCondition::class.java)
    override val conditionName = "if_no_activity_for_dd_hh_mm"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val thresholdDuration = when (conditionValue) {
            is String -> parseDuration(conditionValue)
            else -> {
                logger.warn("    [if_no_activity_for_dd_hh_mm] Invalid condition value type: ${conditionValue::class.simpleName}, skipping bid")
                return false
            }
        } ?: run {
            logger.warn("    [if_no_activity_for_dd_hh_mm] Failed to parse duration format: $conditionValue, skipping bid")
            return false
        }

        // Get the most recent bid
        val lastBid = context.allBids.firstOrNull()
        if (lastBid == null) {
            logger.info("    [if_no_activity_for_dd_hh_mm] No bids yet, allowing bid")
            return true
        }

        val timeSinceLastBid = Duration.between(lastBid.timeStamp, context.currentTime)
        
        // Only bid if there's been no activity for the threshold duration
        val canBid = timeSinceLastBid >= thresholdDuration
        
        logger.info("    [if_no_activity_for_dd_hh_mm] Time since last bid: ${timeSinceLastBid.toMinutes()}min, Threshold: ${thresholdDuration.toMinutes()}min → $canBid")
        
        return canBid
    }

    /**
     * Parse duration string in format "dd_hh_mm"
     * Example: "2_3_30" = 2 days, 3 hours, 30 minutes
     */
    private fun parseDuration(value: String): Duration? {
        return try {
            val parts = value.split("_")
            if (parts.size != 3) return null
            
            val days = parts[0].toLongOrNull() ?: return null
            val hours = parts[1].toLongOrNull() ?: return null
            val minutes = parts[2].toLongOrNull() ?: return null
            
            Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
        } catch (e: Exception) {
            null
        }
    }
}