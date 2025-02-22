package org.example.bidverse_backend.services

import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.repositories.AuctionRepository
import org.springframework.stereotype.Service

@Service
class AuctionService(private val auctionRepository: AuctionRepository) {
    fun getAuctions() = auctionRepository.findAll()
    fun getAuctionById(id: Long) = auctionRepository.findById(id)

    fun getAuctionsByStatus(status: String): List<Auction> {
        return auctionRepository.findByStatus(status)
    }

}