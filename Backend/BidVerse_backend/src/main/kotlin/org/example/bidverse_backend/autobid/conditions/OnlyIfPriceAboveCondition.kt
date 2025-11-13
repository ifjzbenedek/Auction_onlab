package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * only_if_price_above: Only if reached a minimum
 * Type: number
 */
@Component
class OnlyIfPriceAboveCondition : ConditionHandler {
    private val logger = LoggerFactory.getLogger(OnlyIfPriceAboveCondition::class.java)
    override val conditionName = "only_if_price_above"

    override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean {
        if (conditionValue == null) return true

        val minPrice = when (conditionValue) {
            is Number -> java.math.BigDecimal(conditionValue.toString())
            else -> return true
        }

        val currentPrice = context.getCurrentPrice()
        val canBid = currentPrice >= minPrice
        
        logger.info("    [only_if_price_above] Current: $currentPrice, Min: $minPrice → $canBid")
        
        return canBid
    }
}
