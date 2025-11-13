package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

/**
 * Condition: react_delay_minutes
 * Don't bid immediately after being outbid, wait X minutes before reacting.
 * This makes the autobid less predictable and more human-like.
 */
@Component
class ReactDelayMinutesCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(ReactDelayMinutesCondition::class.java)
    
    override val conditionName = "react_delay_minutes"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        val delayMinutes = when (conditionValue) {
            is Number -> conditionValue.toLong()
            is String -> conditionValue.toLongOrNull()
            else -> null
        } ?: run {
            logger.warn("Invalid react_delay_minutes value: $conditionValue")
            return true // Don't block bidding if config is invalid
        }

        if (delayMinutes <= 0) {
            return true // No delay needed
        }

        // Get the timestamp of the last bid that outbid us
        val lastBidByOthers = context.getLastBidByOthers()
        if (lastBidByOthers == null) {
            // No competing bids yet, can bid
            logger.info("No competing bids yet, can bid immediately")
            return true
        }

        // Calculate time since last competing bid
        val lastBidTime = lastBidByOthers.timeStamp
        val now = LocalDateTime.now()
        val timeSinceLastBid = Duration.between(lastBidTime, now).toMinutes()

        logger.info("Last bid by others was $timeSinceLastBid minutes ago (delay required: $delayMinutes minutes)")

        if (timeSinceLastBid < delayMinutes) {
            val remainingDelay = delayMinutes - timeSinceLastBid
            logger.info("Not enough time passed. Waiting $remainingDelay more minutes before reacting")
            return false
        }

        logger.info("Delay satisfied, can react now")
        return true
    }
}
