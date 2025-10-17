package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * counter_bid_factor: If someone raised 1000, you raise 1.2x
 * Type: number
 * Example: 1.2
*/
@Component
class CounterBidFactorCondition : ConditionHandler {
    override val conditionName = "counter_bid_factor"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue == null) return null

        val factor = when (conditionValue) {
            is Number -> BigDecimal(conditionValue.toString())
            else -> return null
        }

        // Get the last two bids to calculate the opponent's increment
        val bids = context.allBids
        if (bids.size < 2) return null

        val lastBid = bids[0]
        val secondLastBid = bids[1]
        
        val opponentIncrement = lastBid.value.subtract(secondLastBid.value)
        val counterIncrement = opponentIncrement.multiply(factor).setScale(0, RoundingMode.HALF_UP)

        return context.getCurrentPrice().add(counterIncrement)
    }
}