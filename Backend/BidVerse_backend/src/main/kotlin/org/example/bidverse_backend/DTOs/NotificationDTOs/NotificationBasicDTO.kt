package org.example.bidverse_backend.DTOs.NotificationDTOs

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import java.time.LocalDateTime

data class NotificationDTO(
    val id: Int,
    val sender: UserBasicDTO?,
    val receiver: UserBasicDTO,
    val auction: AuctionBasicDTO?,
    val createdAt: LocalDateTime,
    val messageText: String,
    val titleText: String?,
    val alreadyOpened: Boolean = false,
)
