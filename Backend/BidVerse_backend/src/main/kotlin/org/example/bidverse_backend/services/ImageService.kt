package org.example.bidverse_backend.services

import com.cloudinary.Cloudinary
import org.example.bidverse_backend.DTOs.AuctionImageDTOs.AuctionImageDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAuctionImageDTO
import org.example.bidverse_backend.Exceptions.*
import org.example.bidverse_backend.Security.SecurityUtils
import org.example.bidverse_backend.repositories.AuctionRepository
import org.example.bidverse_backend.repositories.ImageRepository
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.*
import javax.imageio.ImageIO
import org.example.bidverse_backend.entities.AuctionImage
import org.springframework.transaction.annotation.Transactional


@Service
class ImageService(
    private val auctionImageRepository: ImageRepository,
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository,
    private val securityUtils: SecurityUtils,
    private val cloudinary: Cloudinary
) {
    companion object {
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
        private const val MAX_FILE_SIZE_MB = 10
    }

    @Transactional
    fun uploadAuctionImages(
        auctionId: Int,
        files: List<MultipartFile>
    ): List<AuctionImageDTO> {
        // Validációk
        if (files.isEmpty()) throw ImageValidationException("At least one image is required")
        if (files.size > 10) throw ImageValidationException("Maximum 10 images allowed")
        files.forEach { validateImageFile(it) }

        // Entitások betöltése
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { AuctionNotFoundException("Auction not found") }
        val currentUser = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found") }

        // Ellenőrizzük, hogy van-e már kép az aukcióhoz
        val isFirstUpload = !auctionImageRepository.existsByAuctionId(auctionId)
        val nextOrderIndex = calculateNextOrderIndex(auctionId)

        // Feltöltjük a képeket és elmentjük az adatbázisba
        val savedImages = files.mapIndexed { index, file ->
            val (imageUrl, publicId, width, height, determinedFormat) = uploadToCloudinary(file)

            val image = AuctionImage(
                auction = auction,
                cloudinaryUrl = imageUrl,
                isPrimary = isFirstUpload && index == 0,
                orderIndex = nextOrderIndex + index,
                uploadedBy = currentUser,
                fileSizeKb = (file.size / 1024).toInt(),
                format = determinedFormat
            )

            auctionImageRepository.save(image) // Minden képet külön mentünk
        }

        return savedImages.map { it.toAuctionImageDTO() }
    }

    fun getAuctionImages(auctionId: Int): List<AuctionImageDTO> {
        if (!auctionRepository.existsById(auctionId)) {
            throw AuctionNotFoundException("Auction not found")
        }
        return auctionImageRepository.findByAuctionIdOrderByOrderIndexAsc(auctionId)
            .map { it.toAuctionImageDTO() }
    }

    fun getImageDetails(imageId: Int): AuctionImageDTO {
        return auctionImageRepository.findById(imageId)
            .orElseThrow { ImageNotFoundException("Image not found") }
            .toAuctionImageDTO()
    }

    private fun uploadToCloudinary(file: MultipartFile): CloudinaryUploadResult {
        return try {
            // 1. Kép méreteinek és formátumának meghatározása
            val (width, height) = getImageDimensions(file)
            val format = determineImageFormat(file)

            // 2. Cloudinary feltöltési paraméterek
            val uploadParams = mutableMapOf(
                "folder" to "auction_images",
                "public_id" to "auction_${System.currentTimeMillis()}_${UUID.randomUUID()}",
                "overwrite" to false,
                "resource_type" to "image",
                "quality" to "auto:good",  // Automatikus minőségoptimalizálás
                "format" to format         // Megtartjuk az eredeti formátumot
            )

            // 3. Képfeltöltés indítása
            val uploadResult = cloudinary.uploader()
                .upload(file.bytes, uploadParams)

            // 4. Eredmény feldolgozása
            CloudinaryUploadResult(
                url = uploadResult["secure_url"] as String,
                publicId = uploadResult["public_id"] as String,
                width = width,
                height = height,
                format = format,
                sizeKb = (file.size / 1024).toInt()
            )
        } catch (e: Exception) {
            throw ImageUploadException("Error during cloudinary upload: ${e.message}")
        }
    }

    private fun determineImageFormat(file: MultipartFile): String {
        return when (file.contentType?.lowercase()) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> file.originalFilename?.substringAfterLast('.')?.lowercase() ?: "jpg"
        }
    }

    private data class CloudinaryUploadResult(
        val url: String,         // HTTPS URL a képre
        val publicId: String,    // Cloudinary public ID
        val width: Int?,         // Kép szélessége
        val height: Int?,        // Kép magassága
        val format: String,      // Kép formátuma (jpg, png, webp)
        val sizeKb: Int          // Fájlméret kilobájtban
    )
    private fun validateImageFile(file: MultipartFile) {
        // 1. Alapvető ellenőrzések
        if (file.isEmpty) {
            throw ImageValidationException("Uploaded file is empty")
        }

        // 2. Méret ellenőrzése (max 10MB)
        val maxFileSizeBytes = 10 * 1024 * 1024 // 10MB
        if (file.size > maxFileSizeBytes) {
            throw ImageValidationException("Image size too big, max 10MB allowed")
        }

        // 3. Tartalomtípus ellenőrzése
        val allowedContentTypes = setOf("image/jpeg", "image/png", "image/webp")
        val contentType = file.contentType?.lowercase()
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            throw ImageValidationException("Only JPG, PNG or WebP image files are allowed")
        }

        // 4. Fájlnév és kiterjesztés ellenőrzése
        val originalFilename = file.originalFilename ?: ""
        val fileExtension = originalFilename.substringAfterLast('.').lowercase()
        val allowedExtensions = setOf("jpg", "jpeg", "png", "webp")

        if (fileExtension.isEmpty() || !allowedExtensions.contains(fileExtension)) {
            throw ImageValidationException("Invalid filename or extension")
        }

        // 5. Kép tartalmának valódi ellenőrzése (opcionális, de ajánlott)
        try {
            val bufferedImage = ImageIO.read(file.inputStream)
            if (bufferedImage == null) {
                throw ImageValidationException("File is not a valid image")
            }
        } catch (e: Exception) {
            throw ImageValidationException("Problem during checking the image: ${e.message}")
        }
    }

    private fun getImageDimensions(file: MultipartFile): Pair<Int, Int> {
        return try {
            // A file.bytes egy byte tömböt ad vissza.
            // Ebből minden alkalommal egy új, független ByteArrayInputStream-et hozunk létre.
            // A .use {} blokk biztosítja, hogy ez az új inputStream lezárásra kerüljön a végén.
            ByteArrayInputStream(file.bytes).use { inputStream ->
                val bufferedImage = ImageIO.read(inputStream)
                    ?: throw ImageValidationException("Invalid image data") // Ha nem sikerül képet olvasni

                // Ellenőrizzük, hogy a kép mérete ésszerű-e (nem nulla vagy negatív)
                if (bufferedImage.width <= 0 || bufferedImage.height <= 0) {
                    throw ImageValidationException("Invalid image dimensions")
                }

                Pair(bufferedImage.width, bufferedImage.height)
            }
        } catch (e: Exception) {
            // Ha bármi más hiba történik a méretek kinyerése közben
            throw ImageValidationException("Error during image dimension extraction: ${e.message}")
        }
    }
    private fun calculateNextOrderIndex(auctionId: Int): Int {
        return try {
            // Megkeressük a legnagyobb orderIndex értéket az aukcióhoz
            (auctionImageRepository.findMaxOrderIndexByAuctionId(auctionId) ?: -1) + 1
        } catch (e: Exception) {
            throw ImageProcessingException("Error during order calculation: ${e.message}")
        }
    }
}