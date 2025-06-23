package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.AuctionImage
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository: JpaRepository<AuctionImage, Int> {

    fun existsByAuctionId(auctionId: Int): Boolean
    fun findTopOrderIndexByAuctionIdOrderByOrderIndexDesc(auctionId: Int): AuctionImage?
    fun findByAuctionIdOrderByOrderIndexAsc(auctionId: Int): List<AuctionImage>
}