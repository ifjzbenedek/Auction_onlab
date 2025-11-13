package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * max_total_bids: Max how many bids to place in total
 * Type: number
 */
@Component
class MaxTotalBidsCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(MaxTotalBidsCondition::class.java)
    override val conditionName = "max_total_bids"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val maxBids = when (conditionValue) {
            is Number -> conditionValue.toInt()
            else -> return true
        }

        val currentBidCount = context.getBidCountForThisAutoBid()
        val canBid = currentBidCount < maxBids
        
        logger.info("    [max_total_bids] Current bids: $currentBidCount, Max: $maxBids → $canBid")
        
        return canBid
    }
}
