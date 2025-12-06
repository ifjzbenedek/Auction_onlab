package org.example.bidverse_backend.services.AuctionUtils

import org.example.bidverse_backend.entities.Auction
import java.time.LocalDateTime

object AllAuctionUtils {
    
    /**
     * Kiszámolja az aukció aktuális státuszát a dátumok alapján.
     * Ez a fő függvény - a státusz mindig számított, sosem tárolt érték.
     */
    fun calculateStatus(startDate: LocalDateTime?, expiredDate: LocalDateTime): String {
        val now = LocalDateTime.now()
        
        // Ha nincs startDate, az aukció azonnal aktív (régi viselkedés)
        val effectiveStartDate = startDate ?: LocalDateTime.MIN
        
        return when {
            now.isBefore(effectiveStartDate) -> "UPCOMING"
            now.isBefore(expiredDate) -> "ACTIVE"
            else -> "CLOSED"
        }
    }
    
    /**
     * Extension function az Auction entitáshoz a könnyebb használatért
     */
    fun Auction.calculatedStatus(): String {
        return calculateStatus(this.startDate, this.expiredDate)
    }
    
    /**
     * Ellenőrzi, hogy az aukció aktív-e (licitálható)
     */
    fun isAuctionActive(startDate: LocalDateTime?, expiredDate: LocalDateTime): Boolean {
        return calculateStatus(startDate, expiredDate) == "ACTIVE"
    }
}