package org.example.bidverse_backend.DTOs.AuctionImageDTOs

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO

data class AuctionImageDTO(
    val id: Int,
    val cloudinaryUrl: String,
    val isPrimary: Boolean,
    val orderIndex: Int,
    val uploadedBy: UserBasicDTO,
    val auction: AuctionBasicDTO,
    val fileSizeKb: Int,
    val format: String,
)