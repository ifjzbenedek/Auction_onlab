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
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageWriteParam


@Service
class ImageService(
    private val auctionImageRepository: ImageRepository,
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository,
    private val securityUtils: SecurityUtils,
    private val cloudinary: Cloudinary
) {
    companion object {
        private const val MAX_FILE_SIZE_MB = 50 // Maximum file size in MB
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

        // Ellenőrizzük, hogy van-e már primary kép
        val hasPrimaryImage = auctionImageRepository.existsByAuctionIdAndIsPrimaryTrue(auctionId)
        val nextOrderIndex = calculateNextOrderIndex(auctionId)

        // Képek előkészítése (minden feltöltés egyszerre)
        val imagesToSave = mutableListOf<AuctionImage>()

        files.forEachIndexed { index, file ->
            try {
                val (imageUrl, publicId, width, height, determinedFormat) = uploadToCloudinary(file)

                val image = AuctionImage(
                    auction = auction,
                    cloudinaryUrl = imageUrl,
                    isPrimary = !hasPrimaryImage && index == 0, // Csak az első új kép lesz primary, ha még nincs
                    orderIndex = nextOrderIndex + index,
                    uploadedBy = currentUser,
                    fileSizeKb = (file.size / 1024).toInt(),
                    format = determinedFormat
                )

                imagesToSave.add(image)

            } catch (e: Exception) {
                // Ha egy kép feltöltése sikertelen, töröljük a már feltöltött képeket
                rollbackCloudinaryUploads(imagesToSave)
                throw ImageUploadException("Failed to upload image ${file.originalFilename}: ${e.message}")
            }
        }

        // Batch mentés az adatbázisba
        val savedImages = try {
            auctionImageRepository.saveAll(imagesToSave)
        } catch (e: Exception) {
            // Ha az adatbázis mentés sikertelen, töröljük a Cloudinary-ből a képeket
            rollbackCloudinaryUploads(imagesToSave)
            throw ImageProcessingException("Failed to save images to database: ${e.message}")
        }

        return savedImages.map { it.toAuctionImageDTO() }
    }

    // Segédmetódus a Cloudinary rollback-hez
    private fun rollbackCloudinaryUploads(images: List<AuctionImage>) {
        images.forEach { image ->
            try {
                // Cloudinary public_id kinyerése az URL-ből vagy külön tárolás
                val publicId = extractPublicIdFromUrl(image.cloudinaryUrl)
                cloudinary.uploader().destroy(publicId, emptyMap<String, Any>())
            } catch (e: Exception) {
                // Log the error, de ne akadályozza meg a rollback folyamatát
                println("Warning: Failed to delete image from Cloudinary: ${e.message}")
            }
        }
    }

    private fun extractPublicIdFromUrl(url: String): String {
        // Példa: https://res.cloudinary.com/demo/image/upload/v1234567890/auction_images/auction_123_uuid.jpg
        // A public_id: auction_images/auction_123_uuid
        return url.substringAfter("/upload/")
            .substringAfter("/") // version eltávolítása ha van
            .substringBeforeLast(".") // fájlkiterjesztés eltávolítása
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
            // 1. Kép méreteinek beállítása
            val resizedImageBytes = resizeImageTo720x720(file)
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
                .upload(resizedImageBytes, uploadParams)

            // 4. Eredmény feldolgozása
            CloudinaryUploadResult(
                url = uploadResult["secure_url"] as String,
                publicId = uploadResult["public_id"] as String,
                width = 720,
                height = 720,
                format = format,
                sizeKb = (resizedImageBytes.size / 1024).toInt()
            )
        } catch (e: Exception) {
            throw ImageUploadException("Error during cloudinary upload: ${e.message}")
        }
    }
    private fun resizeImageTo720x720(file: MultipartFile): ByteArray {
        return try {
            val originalImage = ImageIO.read(ByteArrayInputStream(file.bytes))
                ?: throw ImageValidationException("Cannot read image for resizing")

            // 720x720-as négyzet létrehozása
            val resizedImage = createSquareImage(originalImage, 720)

            // Byte array-re konvertálás
            val outputStream = ByteArrayOutputStream()
            val format = determineImageFormat(file)

            when (format.lowercase()) {
                "jpg", "jpeg" -> {
                    // JPEG minőséges kompresszió
                    val writers = ImageIO.getImageWritersByFormatName("jpeg")
                    val writer = writers.next()
                    val writeParam = writer.defaultWriteParam
                    writeParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
                    writeParam.compressionQuality = 0.85f // 85% minőség

                    ImageIO.createImageOutputStream(outputStream).use { ios ->
                        writer.output = ios
                        writer.write(null, IIOImage(resizedImage, null, null), writeParam)
                    }
                    writer.dispose()
                }
                "png" -> {
                    ImageIO.write(resizedImage, "PNG", outputStream)
                }
                else -> {
                    ImageIO.write(resizedImage, "JPEG", outputStream)
                }
            }

            outputStream.toByteArray()
        } catch (e: Exception) {
            throw ImageProcessingException("Image resizing failed: ${e.message}")
        }
    }

    private fun createSquareImage(originalImage: BufferedImage, targetSize: Int): BufferedImage {
        val originalWidth = originalImage.width
        val originalHeight = originalImage.height

        // Négyzet létrehozása fehér háttérrel
        val squareImage = BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB)
        val graphics = squareImage.createGraphics()

        // Minőségi renderelés beállítása
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Fehér háttér
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, targetSize, targetSize)

        // Kép arányának megtartása
        val scale = minOf(
            targetSize.toDouble() / originalWidth,
            targetSize.toDouble() / originalHeight
        )

        val scaledWidth = (originalWidth * scale).toInt()
        val scaledHeight = (originalHeight * scale).toInt()

        // Központozás
        val x = (targetSize - scaledWidth) / 2
        val y = (targetSize - scaledHeight) / 2

        // Kép rajzolása
        graphics.drawImage(originalImage, x, y, scaledWidth, scaledHeight, null)
        graphics.dispose()

        return squareImage
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

        // 2. Méret ellenőrzése (max 50MB)
        val maxFileSizeBytes = MAX_FILE_SIZE_MB * 1024 * 1024
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
            // Az AuctionImage objektumból kinyerjük az orderIndex értéket
            val maxOrderImage = auctionImageRepository.findTopOrderIndexByAuctionIdOrderByOrderIndexDesc(auctionId)
            (maxOrderImage?.orderIndex ?: -1) + 1
        } catch (e: Exception) {
            throw ImageProcessingException("Error during order calculation: ${e.message}")
        }
    }
}