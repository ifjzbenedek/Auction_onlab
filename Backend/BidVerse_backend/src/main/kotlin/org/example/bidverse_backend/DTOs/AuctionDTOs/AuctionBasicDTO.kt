package org.example.bidverse_backend.DTOs.AuctionDTOs

import org.example.bidverse_backend.DTOs.CategoryDTOs.CategoryDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import java.math.BigDecimal
import java.time.LocalDateTime

data class AuctionBasicDTO(
    val id: Int,
    val user: UserBasicDTO,
    val category: CategoryDTO,
    val itemName: String,
    val minimumPrice: BigDecimal,
    val status: String,
    val createDate: LocalDateTime?,
    val expiredDate: LocalDateTime,
    val lastBid: BigDecimal?,
    val description: String,
    val type: String,
    val extraTime: Int?,
    val itemState: String,
    val tags: String?,
    val minStep: Int?,
    val condition: Int,
    val startDate: LocalDateTime?
)
