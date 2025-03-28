package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface AuctionRepository : JpaRepository<Auction, Int>{

    fun findByStatus(status: String): List<Auction>

    fun findByCategoryIn(category: List<Category>): List<Auction>

    fun findByOwner(user: User): List<Auction>

}