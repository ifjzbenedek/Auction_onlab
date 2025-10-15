package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component

/**
 * if_outbid: Only bid if outbid
 * Type: boolean
 */
@Component
class IfOutbidCondition : ConditionHandler {
    override val conditionName = "if_outbid"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue != true) {
            return true // Condition not active
        }

        // Only bid if the user has been outbid
        return context.isOutbid()
    }
}
