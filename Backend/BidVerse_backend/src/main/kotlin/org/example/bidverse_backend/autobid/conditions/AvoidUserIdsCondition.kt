package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * avoid_user_ids: Don't bid against these (e.g. friends)
 * Type: array of integers

@Component
class AvoidUserIdsCondition : ConditionHandler {
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

        val currentHighestBidderId = context.currentHighestBid?.user?.id
        return currentHighestBidderId !in avoidUserIds
    }
}
*/