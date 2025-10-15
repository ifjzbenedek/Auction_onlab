package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import java.math.BigDecimal

/**
 * Interface for condition handlers.
 * Each condition from conditionsJson should have its own implementation.
 */
interface ConditionHandler {
    /**
     * Name of the condition (must match the key in conditionsJson)
     */
    val conditionName: String

    /**
     * Check if this condition allows bidding
     * 
     * @return true if bidding is allowed, false otherwise
     */
    fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean

    /**
     * Modify the bid amount based on this condition
     * 
     * @param baseAmount The base amount before this condition
     * @return The modified amount, or null if no modification
     */
    fun modifyBidAmount(
        context: AutoBidContext,
        conditionValue: Any?,
        baseAmount: BigDecimal
    ): BigDecimal? = null
}
