package org.example.bidverse_backend.services

import org.springframework.transaction.annotation.Transactional
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionStatusResponseDTO
import org.example.bidverse_backend.DTOs.BidDTOs.BidBasicDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionBasicDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionCardDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionStatusResponseDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toBidBasicDTO
import org.example.bidverse_backend.Exceptions.*
import org.example.bidverse_backend.Security.SecurityUtils
import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.entities.Bid
import org.example.bidverse_backend.repositories.*
import org.example.bidverse_backend.services.AuctionUtils.AllAuctionUtils
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.retry.annotation.Backoff
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile


@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository,
    private val bidRepository: BidRepository,
    private val categoryRepository: CategoryRepository,
    private val watchRepository: WatchRepository,
    private val securityUtils: SecurityUtils,
    private val restTemplate: RestTemplate

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
            status = AllAuctionUtils.getStatusWhenCreatingAuction(auctionBasic.createDate),
            createDate = auctionBasic.createDate,
            expiredDate = auctionBasic.expiredDate,
            description = auctionBasic.description,
            type = auctionBasic.type,
            itemState = auctionBasic.itemState,
            tags = auctionBasic.tags,
            minStep = auctionBasic.minStep,
            extraTime = auctionBasic.extraTime,
            lastBid = auctionBasic.lastBid,
            condition = auctionBasic.condition,
            startDate = auctionBasic.startDate
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

        if(auctionBasic.description.contains(auction.description))
            auction.description = auctionBasic.description
        else
            throw NotOnlyExtraDescriptionException("Original description can only be extended.")

        return auctionRepository.save(auction).toAuctionBasicDTO()
    }

    fun updateAuctionStatus(auctionId: Int, newStatus: String): AuctionStatusResponseDTO {
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { AuctionNotFoundException("Auction not found.") }

        if (!AllAuctionUtils.isValidStatusChange(auction.status, newStatus)) {
            throw InvalidAuctionDataException("Invalid status change from ${auction.status} to $newStatus.")
        }

        auction.status = newStatus
        return auctionRepository.save(auction).toAuctionStatusResponseDTO()
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


    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100)
    )
    @Transactional
    fun placeBid(auctionId: Int, bidValue: BigDecimal): Bid {

        // 1. Pesszimista zárolás az aukcióra
        val auction = auctionRepository.findAuctionWithLock(auctionId)
            ?: throw AuctionNotFoundException("Auction not found.")

        if (auction.status != "ACTIVE") {
            throw InvalidAuctionDataException("Auction is not active.")
        }

        // 2. Pesszimista zárolás a jelenlegi nyerő licitre
        val currentWinningBid = bidRepository.findCurrentWinningBidWithLock(auctionId)

        // 3. Validációk
        val currentUserId = securityUtils.getCurrentUserId()
        if (auction.owner.id == currentUserId) {
            throw InvalidBidException("You cannot bid on your own auction.")
        }

        val minStep = auction.minStep?.toBigDecimal() ?: BigDecimal.ZERO
        val requiredMinimum = currentWinningBid?.value?.add(minStep) ?: auction.minimumPrice

        if (bidValue <= requiredMinimum) {
            throw InvalidBidException("Bid amount must be higher than ${requiredMinimum}.")
        }

        // 4. Jelenlegi nyerő frissítése (optimista zárolás használatával)
        currentWinningBid?.let {
            it.isWinning = false
            // Itt NEM kell explicit save, mert a tranzakció végén automatikusan flush lesz
        }

        // 5. Új licit létrehozása
        val bidder = userRepository.getReferenceById(currentUserId)

        val newBid = Bid(
            auction = auction,
            bidder = bidder,
            value = bidValue,
            timeStamp = LocalDateTime.now(),
            isWinning = true
        )

        // 6. Aukció frissítése
        auction.lastBid = bidValue

        return bidRepository.save(newBid)
    }

    fun generateAuctionDescription(images: List<MultipartFile>): String {
        if (images.isEmpty()) throw IllegalArgumentException("No images provided")
        images.forEach { file ->
            if (file.isEmpty || file.contentType?.startsWith("image/") != true) {
                throw IllegalArgumentException("Invalid image file: ${file.originalFilename}")
            }
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.MULTIPART_FORM_DATA
        }

        val body = LinkedMultiValueMap<String, Any>().apply {
            images.forEach { add("images", it.resource) }
        }

        return restTemplate.postForObject(
            "http://localhost:5000/generate-description",
            HttpEntity(body, headers),
            String::class.java
        ) ?: throw IllegalStateException("Empty response from AI service")
    }
}

