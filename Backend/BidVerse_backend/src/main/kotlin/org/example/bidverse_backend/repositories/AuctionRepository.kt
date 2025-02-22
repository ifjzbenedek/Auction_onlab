package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface AuctionRepository : JpaRepository<Auction, Long>{

    fun findByStatus(status: String): List<Auction>

    fun findByCategoryIdIn(category: List<Category>): List<Auction>

    fun findByUserId(user: User): List<Auction>

    fun findByStatusAndExpiredDate(status: String, date: LocalDateTime): List<Auction>

    /*
    @Query(value = """
        SELECT a.*
        FROM Auction a
        WHERE FIND_IN_SET(:tag, a.tags) > 0
        ORDER BY (
            SELECT COUNT(*)
            FROM Auction a2
            WHERE FIND_IN_SET(:tag, a2.tags) > 0
            AND a2.itemId = a.itemId
        ) DESC
    """, nativeQuery = true)
    fun findByTagCountDescending(tags: String): List<Auction>

    @Query("SELECT b FROM Bid b WHERE b.auction = :auction ORDER BY b.amount DESC")
    fun findHighestBid(@Param("auction") auction: Auction): Bid?

     */

}