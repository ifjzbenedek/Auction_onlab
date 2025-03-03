package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.entities.Bid
import org.example.bidverse_backend.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface BidRepository : JpaRepository<Bid, Int> {
    fun findByAuction(itemId: Auction): List<Bid>
    fun findByBidder(userId: User): List<Bid>
}