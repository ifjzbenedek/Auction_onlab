package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.NotificationDTOs.NotificationDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toNotificationDTO
import org.example.bidverse_backend.Exceptions.NotificationNotFoundException
import org.example.bidverse_backend.Exceptions.NotificationPermissionDeniedException
import org.example.bidverse_backend.Exceptions.NotificationAuctionNotFoundException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.Security.SecurityUtils
import org.example.bidverse_backend.entities.Notification
import org.example.bidverse_backend.repositories.NotificationRepository
import org.example.bidverse_backend.repositories.UserRepository
import org.example.bidverse_backend.repositories.AuctionRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val auctionRepository: AuctionRepository,
    private val securityUtils: SecurityUtils
) {

    fun createNotification(notificationDTO: NotificationDTO): NotificationDTO {
        val receiver = userRepository.findById(notificationDTO.receiver.id)
            .orElseThrow { UserNotFoundException("Receiver user not found.") }

        // If sender is not provided, use the current logged-in user as sender
        val sender = if (notificationDTO.sender != null) {
            userRepository.findById(notificationDTO.sender.id)
                .orElseThrow { UserNotFoundException("Sender user not found.") }
        } else {
            // Get current logged-in user as sender
            val currentUserId = securityUtils.getCurrentUserId()
            userRepository.findById(currentUserId)
                .orElseThrow { UserNotFoundException("Current user not found.") }
        }

        val auction = notificationDTO.auction?.let {
            auctionRepository.findById(it.id)
                .orElseThrow { NotificationAuctionNotFoundException("Auction not found.") }
        }

        val notification = Notification(
            sender = sender,
            receiver = receiver,
            auction = auction,
            createdAt = LocalDateTime.now(),
            messageText = notificationDTO.messageText,
            titleText = notificationDTO.titleText,
            alreadyOpened = false
        )

        val savedNotification = notificationRepository.save(notification)
        return savedNotification.toNotificationDTO()
    }

    fun getAllNotificationsByUser(): List<NotificationDTO> {
        val userId = securityUtils.getCurrentUserId()
        val notifications = notificationRepository.findByReceiverId(userId)
        return notifications.map { it.toNotificationDTO() }
    }

    fun getNotificationById(notificationId: Int): NotificationDTO {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { NotificationNotFoundException("Notification not found.") }

        val currentUserId = securityUtils.getCurrentUserId()
        if (notification.receiver.id != currentUserId) {
            throw NotificationPermissionDeniedException("You do not have permission to view this notification.")
        }

        if (!notification.alreadyOpened) {
            notification.alreadyOpened = true
            notificationRepository.save(notification)
        }

        return notification.toNotificationDTO()
    }

    fun deleteNotification(notificationId: Int) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { NotificationNotFoundException("Notification not found.") }

        val currentUserId = securityUtils.getCurrentUserId()
        if (notification.receiver.id != currentUserId) {
            throw NotificationPermissionDeniedException("You do not have permission to delete this notification.")
        }

        notificationRepository.delete(notification)
    }
}
