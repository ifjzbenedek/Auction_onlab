package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.AutoBid
import org.springframework.data.jpa.repository.JpaRepository

interface AutoBidRepository : JpaRepository<AutoBid, Int> {
    fun findByUserId(userId: Int): List<AutoBid>
    fun findByAuctionId(auctionId: Int): List<AutoBid>
    fun findByUserIdAndAuctionId(userId: Int, auctionId: Int): AutoBid?
}
