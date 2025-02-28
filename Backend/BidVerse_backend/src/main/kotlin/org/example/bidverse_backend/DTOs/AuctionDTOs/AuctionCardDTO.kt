package org.example.bidverse_backend.DTOs.AuctionDTOs

import java.math.BigDecimal
import java.time.LocalDateTime

data class AuctionCardDTO(
    val id: Int,
    val itemName: String,
    val createDate: LocalDateTime,
    val expiredDate: LocalDateTime,
    val lastBid: BigDecimal?
)