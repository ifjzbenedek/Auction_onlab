package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * if_outbid: Only bid if outbid
 * Type: boolean
 */
@Component
class IfOutbidCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(IfOutbidCondition::class.java)
    override val conditionName = "if_outbid"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue != true) {
            logger.debug("    [if_outbid] Condition not active, allowing bid")
            return true // Condition not active
        }

        // Only bid if the user has been outbid
        val isOutbid = context.isOutbid()
        val hasUserBid = context.lastBidByThisAutoBid != null
        val isWinning = context.isUserWinning()
        
        logger.info("    [if_outbid] Has user bid: $hasUserBid, Is winning: $isWinning, Is outbid: $isOutbid → $isOutbid")
        
        return isOutbid
    }
}
