package org.example.bidverse_backend.autobid

import java.math.BigDecimal

/**
 * Result of autobid processing.
 * Indicates whether a bid should be placed and the amount.
 */
sealed class AutoBidDecision {
    /**
     * Place a bid with the specified amount
     */
    data class PlaceBid(
        val amount: BigDecimal,
        val reason: String
    ) : AutoBidDecision()

    /**
     * Do not place a bid
     */
    data class SkipBid(
        val reason: String
    ) : AutoBidDecision()

    /**
     * Stop the autobid (deactivate it)
     */
    data class StopAutoBid(
        val reason: String
    ) : AutoBidDecision()
}
