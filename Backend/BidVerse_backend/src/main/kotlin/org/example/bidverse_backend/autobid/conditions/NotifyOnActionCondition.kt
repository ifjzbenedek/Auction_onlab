package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.entities.Notification
import org.example.bidverse_backend.repositories.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Condition: notify_on_action
 * If set to true, the system will send a notification to the user
 * whenever this AutoBid takes an action (places a bid).
 * 
 * The notification is sent during the bid amount modification phase,
 * which ensures it only happens when a bid is actually being placed.
 */
@Component
class NotifyOnActionCondition(
    private val notificationRepository: NotificationRepository
) : ConditionHandler {
    private val logger = LoggerFactory.getLogger(NotifyOnActionCondition::class.java)
    
    override val conditionName = "notify_on_action"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        // This condition never blocks bidding
        // It's just a marker/configuration flag
        val shouldNotify = when (conditionValue) {
            is Boolean -> conditionValue
            is String -> conditionValue.toBoolean()
            else -> false
        }

        if (shouldNotify) {
            logger.info("notify_on_action is enabled - user will be notified when bid is placed")
        }

        // Always return true - this is just a marker/configuration flag
        return true
    }

    /**
     * This method is called when a bid is about to be placed.
     * We use this opportunity to send the notification.
     */
    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        val shouldNotify = when (conditionValue) {
            is Boolean -> conditionValue
            is String -> conditionValue.toBoolean()
            else -> false
        }

        if (shouldNotify) {
            try {
                val notification = Notification(
                    id = null,
                    receiver = context.autoBid.user,
                    sender = null, // System notification
                    auction = context.auction,
                    titleText = "AutoBid Action",
                    messageText = "Your AutoBid is about to place a bid of $baseAmount on '${context.auction.itemName}'.",
                    createdAt = LocalDateTime.now(),
                    alreadyOpened = false
                )
                notificationRepository.save(notification)
                logger.info("Notification sent to user ${context.autoBid.user.id} for AutoBid action on auction ${context.auction.id}")
            } catch (e: Exception) {
                logger.error("Failed to send notification: ${e.message}", e)
                // Don't fail the bidding process if notification fails
            }
        }

        // Don't modify the bid amount, just return null
        return null
    }
}
