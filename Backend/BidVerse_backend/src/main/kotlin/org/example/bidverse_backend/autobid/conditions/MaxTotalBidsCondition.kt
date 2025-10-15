package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * max_total_bids: Max how many bids to place in total
 * Type: number
 */
@Component
class MaxTotalBidsCondition : ConditionHandler {
    override val conditionName = "max_total_bids"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val maxBids = when (conditionValue) {
            is Number -> conditionValue.toInt()
            else -> return true
        }

        val currentBidCount = context.getBidCountForThisAutoBid()
        return currentBidCount < maxBids
    }
}
