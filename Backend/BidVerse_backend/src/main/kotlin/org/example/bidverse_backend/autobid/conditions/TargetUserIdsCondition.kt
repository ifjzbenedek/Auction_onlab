package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * target_user_ids: Only compete with these (e.g. rival)
 * Type: array of integers
 */
@Component
class TargetUserIdsCondition : ConditionHandler {
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

        val currentHighestBidderId = context.currentHighestBid?.user?.id
        
        // If no highest bid yet, or it's not from a target user, don't bid
        return currentHighestBidderId in targetUserIds
    }
}
