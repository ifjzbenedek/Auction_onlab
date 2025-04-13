package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.Bid
import org.springframework.data.jpa.repository.JpaRepository

interface BidRepository : JpaRepository<Bid, Int> {
    fun findByBidderId(userId: Int): List<Bid>
    fun findByAuctionId(auctionId: Int): List<Bid>


}