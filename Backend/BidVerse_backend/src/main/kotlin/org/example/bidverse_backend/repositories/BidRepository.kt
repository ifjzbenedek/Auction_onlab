package org.example.bidverse_backend.repositories

import jakarta.persistence.LockModeType
import org.example.bidverse_backend.entities.Bid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BidRepository : JpaRepository<Bid, Int> {
    fun findByBidderId(userId: Int): List<Bid>
    fun findByAuctionId(auctionId: Int): List<Bid>
    fun findByAuctionIdOrderByValueDesc(auctionId: Int): List<Bid>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId AND b.isWinning = true")
    fun findCurrentWinningBidWithLock(@Param("auctionId") auctionId: Int): Bid?
}