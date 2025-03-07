package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.entities.*
import org.example.bidverse_backend.services.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


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
}
