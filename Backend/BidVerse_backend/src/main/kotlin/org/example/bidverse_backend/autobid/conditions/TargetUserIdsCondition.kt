package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * target_user_ids: Only compete with these (e.g. rival)
 * Type: array of integers
 */
@Component
class TargetUserIdsCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(TargetUserIdsCondition::class.java)
    override val conditionName = "target_user_ids"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val targetUserIds = when (conditionValue) {
            is List<*> -> conditionValue.mapNotNull { 
                when (it) {
                    is Number -> it.toInt()
                    else -> null
                }
            }
            else -> return true
        }

        val currentHighestBidderId = context.currentHighestBid?.bidder?.id
        
        // If no highest bid yet, or it's not from a target user, don't bid
        val canBid = currentHighestBidderId in targetUserIds
        
        logger.info("    [target_user_ids] Current highest bidder: $currentHighestBidderId, Targets: $targetUserIds → $canBid")
        
        return canBid
    }
}
