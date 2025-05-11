package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.AuctionImageDTOs.AuctionImageDTO
import org.example.bidverse_backend.Exceptions.AuctionNotFoundException
import org.example.bidverse_backend.Exceptions.ImageNotFoundException
import org.example.bidverse_backend.Exceptions.ImageValidationException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.services.ImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/auctions/{auctionId}/images")
class ImageController(
    private val imageService: ImageService
){
    @PostMapping
    fun uploadImages(
        @PathVariable auctionId: Int,
        @RequestParam files: List<MultipartFile>
    ): ResponseEntity<Any> {
        return try {
            val uploadedImages = imageService.uploadAuctionImages(auctionId, files)
            ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages)
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: ImageValidationException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }


    @GetMapping
    fun getAuctionImages(@PathVariable auctionId: Int): ResponseEntity<Any> {
        return try {
            val images = imageService.getAuctionImages(auctionId)
            ResponseEntity.ok(images)
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @GetMapping("/{imageId}")
    fun getImageDetails(
        @PathVariable auctionId: Int,
        @PathVariable imageId: Int
    ): ResponseEntity<Any> {
        return try {
            val image = imageService.getImageDetails(imageId)
            ResponseEntity.ok(image)
        } catch (e: ImageNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }
}