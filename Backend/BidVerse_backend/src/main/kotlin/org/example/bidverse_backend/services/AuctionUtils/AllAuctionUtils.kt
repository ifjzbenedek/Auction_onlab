package org.example.bidverse_backend.services.AuctionUtils

import java.time.LocalDateTime

object AllAuctionUtils {
    fun getStatusWhenCreatingAuction(startDate: LocalDateTime?): String {
        if (startDate == null) {
            return "ACTIVE"
        }
        val auctionStartedInstantly = startDate.isBefore(LocalDateTime.now())

        return if(auctionStartedInstantly)
             "ACTIVE"
        else
             "UPCOMING"

    }

    fun isValidStatusChange(oldStatus: String, newStatus: String): Boolean {
        return when (oldStatus) {
            "ACTIVE" -> newStatus == "CLOSED" || newStatus == "EXPIRED" || newStatus == "UPCOMING" || newStatus == "FINISHED"
            "UPCOMING" -> newStatus == "ACTIVE"
            "CLOSED" -> false
            "EXPIRED" -> false
            else -> false
        }
    }
}