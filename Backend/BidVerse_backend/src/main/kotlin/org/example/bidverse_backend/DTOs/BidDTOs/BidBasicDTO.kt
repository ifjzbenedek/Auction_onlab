package org.example.bidverse_backend.DTOs.BidDTOs

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import java.math.BigDecimal
import java.time.LocalDateTime


class BidBasicDTO(
    val id: Int,
    val auction: AuctionBasicDTO,
    val bidder: UserBasicDTO,
    val value: BigDecimal,
    val timeStamp: LocalDateTime,
    val isWinning: Boolean
)