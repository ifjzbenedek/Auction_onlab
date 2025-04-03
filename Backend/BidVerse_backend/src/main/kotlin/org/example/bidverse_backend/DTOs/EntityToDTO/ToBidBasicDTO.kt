package org.example.bidverse_backend.DTOs.EntityToDTO

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.BidDTOs.BidBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.entities.Bid
import org.example.bidverse_backend.extensions.toUserBasicDTO
import org.example.bidverse_backend.extensions.toUserCredentialsDTO
import java.math.BigDecimal
import java.time.LocalDateTime

fun Bid.toBidBasicDTO(): BidBasicDTO {
    return BidBasicDTO(
        id = this.id!!,
        bidder = this.bidder.toUserCredentialsDTO(),
        value = this.value,
        timeStamp = this.timeStamp,
        isWinning = this.isWinning
    )
}