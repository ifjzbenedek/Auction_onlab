package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * avoid_user_ids: Don't bid against these (e.g. friends)
 * Type: array of integers
 */
@Component
class AvoidUserIdsCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(AvoidUserIdsCondition::class.java)
    override val conditionName = "avoid_user_ids"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val avoidUserIds = when (conditionValue) {
            is List<*> -> conditionValue.mapNotNull { 
                when (it) {
                    is Number -> it.toInt()
                    else -> null
                }
            }
            else -> return true
        }

        val currentHighestBidderId = context.currentHighestBid?.bidder?.id
        val canBid = currentHighestBidderId !in avoidUserIds
        
        logger.info("    [avoid_user_ids] Current highest bidder: $currentHighestBidderId, Avoid: $avoidUserIds → $canBid")
        
        return canBid
    }
}
