package org.example.bidverse_backend.repositories

import jakarta.persistence.LockModeType
import org.example.bidverse_backend.entities.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface AuctionRepository : JpaRepository<Auction, Int>{

    fun findByCategoryIn(category: List<Category>): List<Auction>

    fun findByOwner(user: User): List<Auction>

    fun findByIdIn(ids: List<Int>): List<Auction>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    fun findAuctionWithLock(@Param("id") id: Int): Auction?
}