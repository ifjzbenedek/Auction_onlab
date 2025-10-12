package org.example.bidverse_backend.DTOs.EntityToDTO

import org.example.bidverse_backend.DTOs.NotificationDTOs.NotificationDTO
import org.example.bidverse_backend.entities.Notification
import org.example.bidverse_backend.extensions.toUserBasicDTO

fun Notification.toNotificationDTO(): NotificationDTO {
    return NotificationDTO(
        id = this.id!!,
        sender = this.sender?.toUserBasicDTO(),
        receiver = this.receiver.toUserBasicDTO(),
        auction = this.auction?.toAuctionBasicDTO(),
        createdAt = this.createdAt,
        messageText = this.messageText,
        titleText = this.titleText,
        alreadyOpened = this.alreadyOpened
    )
}