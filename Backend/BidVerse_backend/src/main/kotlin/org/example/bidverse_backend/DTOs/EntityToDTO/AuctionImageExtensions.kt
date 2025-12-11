package org.example.bidverse_backend.DTOs.EntityToDTO

import org.example.bidverse_backend.DTOs.AuctionImageDTOs.AuctionImageDTO
import org.example.bidverse_backend.entities.AuctionImage
import org.example.bidverse_backend.extensions.toUserBasicDTO

fun AuctionImage.toAuctionImageDTO(): AuctionImageDTO {
    return AuctionImageDTO(
        id = this.id!!,
        cloudinaryUrl = this.cloudinaryUrl,
        isPrimary = this.isPrimary,
        auction = this.auction.toAuctionBasicDTO(),
        uploadedBy = this.uploadedBy.toUserBasicDTO(),
        format = this.format,
        fileSizeKb = this.fileSizeKb,
        orderIndex = this.orderIndex
    )
}