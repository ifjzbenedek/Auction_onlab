package org.example.bidverse_backend.services.AuctionUtils

import org.example.bidverse_backend.entities.Auction
import java.time.LocalDateTime

object AllAuctionUtils {
    
    /**
     * Calculates the current status of the auction based on dates.
     * This is the main function - status is always calculated, never stored.
     */
    fun calculateStatus(startDate: LocalDateTime?, expiredDate: LocalDateTime): String {
        val now = LocalDateTime.now()
        
        // If no startDate, auction is immediately active (legacy behavior)
        val effectiveStartDate = startDate ?: LocalDateTime.MIN
        
        return when {
            now.isBefore(effectiveStartDate) -> "UPCOMING"
            now.isBefore(expiredDate) -> "ACTIVE"
            else -> "CLOSED"
        }
    }
    
    /**
     * Extension function for the Auction entity for easier usage
     */
    fun Auction.calculatedStatus(): String {
        return calculateStatus(this.startDate, this.expiredDate)
    }
    
    /**
     * Checks if the auction is active (biddable)
     */
    fun isAuctionActive(startDate: LocalDateTime?, expiredDate: LocalDateTime): Boolean {
        return calculateStatus(startDate, expiredDate) == "ACTIVE"
    }
}