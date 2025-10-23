package org.example.bidverse_backend.autobid

import org.example.bidverse_backend.entities.AutoBid
import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.entities.Bid
import org.example.bidverse_backend.entities.User
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Context for autobid processing.
 * Contains all information needed to decide whether to bid and how much.
 */
data class AutoBidContext(
    val autoBid: AutoBid,
    val auction: Auction,
    val user: User,
    val currentHighestBid: Bid?,
    val allBids: List<Bid>,
    val currentTime: LocalDateTime,
    val lastBidByThisAutoBid: Bid? = null
) {
    /**
     * Current price of the auction (highest bid or minimum price)
     */
    fun getCurrentPrice(): BigDecimal {
        if (currentHighestBid != null) {
            return currentHighestBid.value
        }
        return auction.minimumPrice
    }

    /**
     * Is the current user the highest bidder?
     */
    fun isUserWinning(): Boolean {
        if (currentHighestBid == null) {
            return false
        }
        return currentHighestBid.bidder.id == user.id
    }

    /**
     * Has this autobid been outbid?
     * Returns true if:
     * - User has never bid (should place initial bid)
     * - User has bid but is no longer winning
     */
    fun isOutbid(): Boolean {
        if (lastBidByThisAutoBid == null) {
            return true
        }
        return !isUserWinning()
    }

    /**
     * Has the auction ended?
     */
    fun isAuctionEnded(): Boolean {
        return currentTime.isAfter(auction.expiredDate)
    }

    /**
     * Minutes until auction ends
     */
    fun getMinutesUntilEnd(): Long {
        val duration = java.time.Duration.between(currentTime, auction.expiredDate)
        return duration.toMinutes()
    }

    /**
     * Seconds until auction ends
     */
    fun getSecondsUntilEnd(): Long {
        val duration = java.time.Duration.between(currentTime, auction.expiredDate)
        return duration.seconds
    }

    /**
     * Total number of bids this autobid has placed
     */
    fun getBidCountForThisAutoBid(): Int {
        var count = 0
        for (bid in allBids) {
            if (bid.bidder.id == user.id) {
                count++
            }
        }
        return count
    }
}
