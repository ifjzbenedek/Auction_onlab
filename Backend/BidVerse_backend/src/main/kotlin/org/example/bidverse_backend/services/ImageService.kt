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
        // Validations
        if (files.isEmpty()) throw ImageValidationException("At least one image is required")
        if (files.size > 10) throw ImageValidationException("Maximum 10 images allowed")
        files.forEach { validateImageFile(it) }

        // Load entities
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { AuctionNotFoundException("Auction not found") }
        val currentUser = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found") }

        // Check if there is already a primary image
        val hasPrimaryImage = auctionImageRepository.existsByAuctionIdAndIsPrimaryTrue(auctionId)
        val nextOrderIndex = calculateNextOrderIndex(auctionId)

        // Prepare images (all uploads at once)
        val imagesToSave = mutableListOf<AuctionImage>()

        files.forEachIndexed { index, file ->
            try {
                val uploadResult = uploadToCloudinary(file)

                val image = AuctionImage(
                    auction = auction,
                    cloudinaryUrl = uploadResult.url,
                    isPrimary = !hasPrimaryImage && index == 0,
                    orderIndex = nextOrderIndex + index,
                    uploadedBy = currentUser,
                    fileSizeKb = uploadResult.sizeKb,
                    format = uploadResult.format
                )

                imagesToSave.add(image)

            } catch (e: Exception) {
                rollbackCloudinaryUploads(imagesToSave)
                throw ImageUploadException("Failed to upload image ${file.originalFilename}: ${e.message}")
            }
        }

        // Batch save to database
        val savedImages = try {
            auctionImageRepository.saveAll(imagesToSave)
        } catch (e: Exception) {
            // If database save fails, delete images from Cloudinary
            rollbackCloudinaryUploads(imagesToSave)
            throw ImageProcessingException("Failed to save images to database: ${e.message}")
        }

        return savedImages.map { it.toAuctionImageDTO() }
    }

    // Helper method for Cloudinary rollback
    fun rollbackCloudinaryUploads(images: List<AuctionImage>) {
        images.forEach { image ->
            try {
                // Extract Cloudinary public_id from URL or store separately
                val publicId = extractPublicIdFromUrl(image.cloudinaryUrl)
                cloudinary.uploader().destroy(publicId, emptyMap<String, Any>())
            } catch (e: Exception) {
                // Silently continue - don't interrupt the rollback process
            }
        }
    }

    private fun extractPublicIdFromUrl(url: String): String {

        return url.substringAfter("/upload/")
            .substringAfter("/") // remove version if present
            .substringBeforeLast(".") // remove file extension
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
            // 1. Set image dimensions
            val resizedImageBytes = resizeImageTo720x720(file)
            val format = determineImageFormat(file)

            // 2. Cloudinary upload parameters
            val uploadParams = mutableMapOf(
                "folder" to "auction_images",
                "public_id" to "auction_${System.currentTimeMillis()}_${UUID.randomUUID()}",
                "overwrite" to false,
                "resource_type" to "image",
                "quality" to "auto:good",  // Automatic quality optimization
                "format" to format         // Keep original format
            )

            // 3. Start image upload
            val uploadResult = cloudinary.uploader()
                .upload(resizedImageBytes, uploadParams)

            // 4. Process result
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

            // Create 720x720 square
            val resizedImage = createSquareImage(originalImage, 720)

            // Convert to byte array
            val outputStream = ByteArrayOutputStream()
            val format = determineImageFormat(file)

            when (format.lowercase()) {
                "jpg", "jpeg" -> {
                    // JPEG quality compression
                    val writers = ImageIO.getImageWritersByFormatName("jpeg")
                    val writer = writers.next()
                    val writeParam = writer.defaultWriteParam
                    writeParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
                    writeParam.compressionQuality = 0.85f // 85% quality

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

        // Create square with white background
        val squareImage = BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB)
        val graphics = squareImage.createGraphics()

        // Set quality rendering
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // White background
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, targetSize, targetSize)

        // Maintain image aspect ratio
        val scale = minOf(
            targetSize.toDouble() / originalWidth,
            targetSize.toDouble() / originalHeight
        )

        val scaledWidth = (originalWidth * scale).toInt()
        val scaledHeight = (originalHeight * scale).toInt()

        // Center the image
        val x = (targetSize - scaledWidth) / 2
        val y = (targetSize - scaledHeight) / 2

        // Draw image
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
        val url: String,         // HTTPS URL to the image
        val publicId: String,    // Cloudinary public ID
        val width: Int?,         // Image width
        val height: Int?,        // Image height
        val format: String,      // Image format (jpg, png, webp)
        val sizeKb: Int          // File size in kilobytes
    )
    private fun validateImageFile(file: MultipartFile) {
        // 1. Basic checks
        if (file.isEmpty) {
            throw ImageValidationException("Uploaded file is empty")
        }

        // 2. Size check (max 50MB)
        val maxFileSizeBytes = MAX_FILE_SIZE_MB * 1024 * 1024
        if (file.size > maxFileSizeBytes) {
            throw ImageValidationException("Image size too big, max 10MB allowed")
        }

        // 3. Content type check
        val allowedContentTypes = setOf("image/jpeg", "image/png", "image/webp")
        val contentType = file.contentType?.lowercase()
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            throw ImageValidationException("Only JPG, PNG or WebP image files are allowed")
        }

        // 4. Filename and extension check
        val originalFilename = file.originalFilename ?: ""
        val fileExtension = originalFilename.substringAfterLast('.').lowercase()
        val allowedExtensions = setOf("jpg", "jpeg", "png", "webp")

        if (fileExtension.isEmpty() || !allowedExtensions.contains(fileExtension)) {
            throw ImageValidationException("Invalid filename or extension")
        }

        // 5. Actual image content validation (optional but recommended)
        try {
            val bufferedImage = ImageIO.read(file.inputStream)
            if (bufferedImage == null) {
                throw ImageValidationException("File is not a valid image")
            }
        } catch (e: Exception) {
            throw ImageValidationException("Problem during checking the image: ${e.message}")
        }
    }

    private fun calculateNextOrderIndex(auctionId: Int): Int {
        return try {
            // Extract orderIndex value from AuctionImage object
            val maxOrderImage = auctionImageRepository.findTopOrderIndexByAuctionIdOrderByOrderIndexDesc(auctionId)
            (maxOrderImage?.orderIndex ?: -1) + 1
        } catch (e: Exception) {
            throw ImageProcessingException("Error during order calculation: ${e.message}")
        }
    }
}