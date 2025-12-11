package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Int> {
    fun findByReceiverId(receiverId: Int): List<Notification>
}
