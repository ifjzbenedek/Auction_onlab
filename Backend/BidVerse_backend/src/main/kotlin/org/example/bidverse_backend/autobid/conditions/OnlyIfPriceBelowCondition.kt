package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * only_if_price_below: Only if current price is less than...
 * Type: number
 */
@Component
class OnlyIfPriceBelowCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(OnlyIfPriceBelowCondition::class.java)
    override val conditionName = "only_if_price_below"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val maxPrice = when (conditionValue) {
            is Number -> java.math.BigDecimal(conditionValue.toString())
            else -> return true
        }

        val currentPrice = context.getCurrentPrice()
        val canBid = currentPrice < maxPrice
        
        logger.info("    [only_if_price_below] Current: $currentPrice, Max: $maxPrice → $canBid")
        
        return canBid
    }
}
