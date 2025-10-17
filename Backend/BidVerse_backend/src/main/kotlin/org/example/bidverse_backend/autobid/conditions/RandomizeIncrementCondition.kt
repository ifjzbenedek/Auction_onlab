package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

/**
 * randomize_increment: Add small random noise to be unpredictable
 * Type: boolean
 */
@Component
class RandomizeIncrementCondition : ConditionHandler {
    override val conditionName = "randomize_increment"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true

    override fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? {
        if (conditionValue != true) return null

        val increment = baseAmount - context.getCurrentPrice()
        
        // Add random noise: -10% to +10% of the increment
        val randomFactor = (Random.nextDouble(-0.1, 0.1))
        val noise = increment.multiply(BigDecimal(randomFactor))
        
        return baseAmount.add(noise).setScale(0, RoundingMode.HALF_UP)
    }
}
