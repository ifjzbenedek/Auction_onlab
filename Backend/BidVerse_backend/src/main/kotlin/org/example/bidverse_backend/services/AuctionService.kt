package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionBasicDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionCardDTO
import org.example.bidverse_backend.Exceptions.CategoryNotFoundException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.repositories.AuctionRepository
import org.example.bidverse_backend.repositories.BidRepository
import org.example.bidverse_backend.repositories.CategoryRepository
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.stereotype.Service
import org.example.bidverse_backend.services.UserService
import java.time.LocalDateTime

@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository,
    private val bidRepository: BidRepository,
    private val categoryRepository: CategoryRepository
) {
    fun getAllAuctions(status: String?, category: String?): List<AuctionCardDTO> {
        return auctionRepository.findAll().filter { auction ->
            (status == null || auction.status == status) && // Szűrés státusz alapján
                    (category == null || auction.category.categoryName == category) // Szűrés kategória neve alapján
        }.map { it.toAuctionCardDTO() }
    }
 /*
    fun createAuction(auctionBasic: AuctionBasicDTO, userId: Int): AuctionBasicDTO {
        val user = userRepository.findById(getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        val category = categoryRepository.findById(auctionBasic.categoryId)
            .orElseThrow { CategoryNotFoundException("Category not found.") }

        val auction = Auction(
            owner = user, // Az `owner` mező egy `User` entitás
            category = category, // A `category` mező egy `Category` entitás
            itemName = auctionBasic.itemName,
            minimumPrice = auctionBasic.minimumPrice,
            status = "ACTIVE",
            createDate = LocalDateTime.now(),
            expiredDate = auctionBasic.expiredDate,
            description = auctionBasic.description,
            type = auctionBasic.type,
            itemState = auctionBasic.itemState,
            tags = auctionBasic.tags,
            minStep = auctionBasic.minStep
        )

        return auctionRepository.save(auction).toAuctionBasicDTO()
    }
    */

}

