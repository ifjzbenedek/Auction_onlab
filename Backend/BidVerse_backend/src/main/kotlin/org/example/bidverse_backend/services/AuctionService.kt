package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO
import org.example.bidverse_backend.DTOs.BidDTOs.BidBasicDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionBasicDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionCardDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toBidBasicDTO
import org.example.bidverse_backend.Exceptions.*
import org.example.bidverse_backend.Security.SecurityUtils
import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.entities.Bid
import org.example.bidverse_backend.repositories.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository,
    private val bidRepository: BidRepository,
    private val categoryRepository: CategoryRepository,
    private val watchRepository: WatchRepository,
    private val securityUtils: SecurityUtils

) {
    fun getAllAuctions(statuses: String?, categories: String?): List<AuctionCardDTO> {
        val statusList = statuses?.split(",") ?: emptyList()
        val categoryList = categories?.split(",") ?: emptyList()

        return auctionRepository.findAll().filter { auction ->
            (statusList.isEmpty() || auction.status in statusList) &&
                    (categoryList.isEmpty() || auction.category.categoryName in categoryList)
        }.map { it.toAuctionCardDTO() }
    }

    fun createAuction(auctionBasic: AuctionBasicDTO): AuctionBasicDTO {
        val user = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        val categoryId = auctionBasic.category.id ?: throw IllegalArgumentException("Category ID must not be null.")

        val category = categoryRepository.findById(categoryId)
            .orElseThrow { CategoryNotFoundException("Category not found.") }

        val auction = Auction(
            owner = user,
            category = category,
            itemName = auctionBasic.itemName,
            minimumPrice = auctionBasic.minimumPrice,
            status = "ACTIVE",
            createDate = LocalDateTime.now(),
            expiredDate = auctionBasic.expiredDate,
            description = auctionBasic.description,
            type = auctionBasic.type,
            itemState = auctionBasic.itemState,
            tags = auctionBasic.tags,
            minStep = auctionBasic.minStep,
            extraTime = auctionBasic.extraTime,
            lastBid = auctionBasic.lastBid,
            condition = auctionBasic.condition
        )

        return auctionRepository.save(auction).toAuctionBasicDTO()
    }

    fun getAuctionById(auctionId: Int): AuctionBasicDTO {
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { UserNotFoundException("Auction not found.") }

        return auction.toAuctionBasicDTO()
    }

    fun updateAuction(auctionId: Int, auctionBasic: AuctionBasicDTO): AuctionBasicDTO
    {
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow{ AuctionNotFoundException("Auction not found.") }

        if(auction.owner.id != securityUtils.getCurrentUserId())
            throw PermissionDeniedException("Permission denied.")

        auction.expiredDate = auctionBasic.expiredDate
        auction.description = auctionBasic.description
        auction.tags = auctionBasic.tags

        return auctionRepository.save(auction).toAuctionBasicDTO()
    }

    fun deleteAuction(auctionId: Int) {
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { AuctionNotFoundException("Auction not found.") }

        if (auction.owner.id != securityUtils.getCurrentUserId()) {
            throw PermissionDeniedException("You do not have permission to delete this auction.")
        }

        auctionRepository.delete(auction)
    }

    fun getCreatedAuctions(): List<AuctionBasicDTO> {
        val user = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        return auctionRepository.findByOwner(user).map { it.toAuctionBasicDTO() }
    }

    fun getWatchedAuctions(): List<AuctionCardDTO> {
        val user = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        val watchedAuctions = watchRepository.findByUserId(user.id!!) // Nem lehet null, mert autogenerált az id a Userhez, és már megtaláltuk a usert
            .map { it.auction }

        return watchedAuctions.map { it.toAuctionCardDTO() }
    }

    fun getBiddedAuctions(): List<AuctionBasicDTO>
    {
        val user = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        val biddedAuctions = bidRepository.findByBidderId(user.id!!)
            .map { it.auction }.distinct()

        return biddedAuctions.map { it.toAuctionBasicDTO() }
    }

    fun getBidsForAuction(auctionId: Int): List<BidBasicDTO> {
        if (!auctionRepository.existsById(auctionId)) {
            throw AuctionNotFoundException("Auction with ID $auctionId not found.")
        }

        return bidRepository.findByAuctionId(auctionId).map { it.toBidBasicDTO() }
    }

    fun placeBid(auctionId: Int, bidValue: BigDecimal): Bid {
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { AuctionNotFoundException("Auction not found.") }

        if (auction.status != "ACTIVE") {
            throw InvalidAuctionDataException("Auction is not active.")
        }

        val currentUserId = securityUtils.getCurrentUserId()
        if (auction.owner.id == currentUserId) {
            throw InvalidBidException("You cannot bid on your own auction.")
        }

        val bids = bidRepository.findByAuctionId(auctionId)

        val currentWinningBid = bids.find { it.isWinning }

        if (currentWinningBid != null && bidValue <= currentWinningBid.value + (auction.minStep?.toBigDecimal() ?: BigDecimal.ZERO)) {
            throw InvalidBidException("Bid amount must be higher than the current highest bid plus the minimum increment.")
        }

        // Jelenlegi nyerőt átállítjuk false-ra
        currentWinningBid?.let {
            it.isWinning = false
            bidRepository.save(it)
        }

        val bidder = userRepository.findById(currentUserId)
            .orElseThrow { UserNotFoundException("User not found.") }

        val newBid = Bid(
            auction = auction,
            bidder = bidder,
            value = bidValue,
            timeStamp = LocalDateTime.now(),
            isWinning = true
        )

        auction.lastBid = bidValue
        auctionRepository.save(auction)

        return bidRepository.save(newBid)
    }
}

