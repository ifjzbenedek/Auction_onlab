package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionCardDTO
import org.example.bidverse_backend.repositories.AuctionRepository
import org.example.bidverse_backend.repositories.BidRepository
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository,
    private val bidRepository: BidRepository
) {
    fun getAllAuctions(status: String?, category: String?): List<AuctionCardDTO> {
        return auctionRepository.findAll().filter { auction ->
            (status == null || auction.status == status) && // Szűrés státusz alapján
                    (category == null || auction.category.categoryName == category) // Szűrés kategória neve alapján
        }.map { it.toAuctionCardDTO() }
    }
}

