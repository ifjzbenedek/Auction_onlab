package org.example.bidverse_backend.DTOs.AuctionDTOs

import java.math.BigDecimal
import java.time.LocalDateTime

data class AuctionBasicDTO(
    val id: Int,
    val userId: Int,
    val categoryId: Int,
    val itemName: String,
    val minimumPrice: BigDecimal,
    val status: String,
    val createDate: LocalDateTime,
    val expiredDate: LocalDateTime,
    val lastBid: BigDecimal?,
    val description: String,
    val type: String,
    val extraTime: LocalDateTime?,
    val itemState: String,
    val tags: String?,
    val minStep: Int?
)
