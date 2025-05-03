package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO
import org.example.bidverse_backend.DTOs.BidDTOs.BidRequestDTO
import org.example.bidverse_backend.Exceptions.*
import org.example.bidverse_backend.entities.*
import org.example.bidverse_backend.services.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/auctions")
class AuctionController(private val auctionService: AuctionService) {

    @GetMapping
    fun getAllAuctions(
        @RequestParam status: String?,
        @RequestParam category: String?
    ): ResponseEntity<List<AuctionCardDTO>> {
        return try {
            val auctions = auctionService.getAllAuctions(status, category)
            ResponseEntity.ok(auctions)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping
    fun createAuction(@RequestBody auctionBasic: AuctionBasicDTO): ResponseEntity<Any> {
        return try {
            val auction = auctionService.createAuction(auctionBasic)
            ResponseEntity.status(HttpStatus.CREATED).body(auction)
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating auction.")
        }
    }

    @GetMapping("/{auctionId}")
    fun getAuctionById(@PathVariable auctionId: Int): ResponseEntity<Any> {
        return try {
            val auction = auctionService.getAuctionById(auctionId)
            ResponseEntity.ok(auction)
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PutMapping("/{auctionId}")
    fun updateAuction(
        @PathVariable auctionId: Int,
        @RequestBody auctionBasic: AuctionBasicDTO
    ): ResponseEntity<Any> {
        return try {
            val auction = auctionService.updateAuction(auctionId, auctionBasic)
            ResponseEntity.ok(auction)
        } catch (e: NotOnlyExtraDescriptionException){
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: PermissionDeniedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        }
    }

    @DeleteMapping("/{auctionId}")
    fun deleteAuction(@PathVariable auctionId: Int): ResponseEntity<Any> {
        return try {
            auctionService.deleteAuction(auctionId)
            ResponseEntity.noContent().build()
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: PermissionDeniedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        }
    }

    @GetMapping("/my/createdAuctions")
    fun getCreatedAuctions(): ResponseEntity<Any> {
        return try {
            val auctions = auctionService.getCreatedAuctions()
            ResponseEntity.ok(auctions)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving auctions.")
        }
    }

    @GetMapping("/my/watchedAuctions")
    fun getWatchedAuctions(): ResponseEntity<Any> {
        return try {
            val auctions = auctionService.getWatchedAuctions()
            ResponseEntity.ok(auctions)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving auctions.")
        }
    }

    @GetMapping("/my/biddedAuctions")
    fun getBiddedAuctions(): ResponseEntity<Any> {
        return try {
            val auctions = auctionService.getBiddedAuctions()
            ResponseEntity.ok(auctions)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving auctions.")
        }
    }

    //Később kell majd followedAuctions

    @GetMapping("/{auctionId}/bids")
    fun getBidsForAuction(@PathVariable auctionId: Int): ResponseEntity<Any> {
        return try {
            val bids = auctionService.getBidsForAuction(auctionId)
            ResponseEntity.ok(bids)
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @PostMapping("/{auctionId}/bids")
    fun placeBid(
        @PathVariable auctionId: Int,
        @RequestBody bidRequest: BidRequestDTO
    ): ResponseEntity<Any> {
        return try {
            val bid = auctionService.placeBid(auctionId, bidRequest.amount)
            ResponseEntity.status(HttpStatus.CREATED).body(bid) // 201 Created
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message) // 404 Not Found
        } catch (e: InvalidAuctionDataException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message) // 400 Bad Request
        } catch (e: InvalidBidException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message) // 400 Bad Request
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message) // 404 Not Found
        }

    }

    @PostMapping("/generate-description")
    fun generateDescription(
        @RequestParam("images") images: Array<MultipartFile>
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(auctionService.generateAuctionDescription(images.toList()))
        } catch (e: DescriptionGenerationException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }
}
