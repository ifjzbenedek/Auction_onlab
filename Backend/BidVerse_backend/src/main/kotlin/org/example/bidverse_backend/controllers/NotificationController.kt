package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.NotificationDTOs.NotificationDTO
import org.example.bidverse_backend.Exceptions.NotificationNotFoundException
import org.example.bidverse_backend.Exceptions.NotificationPermissionDeniedException
import org.example.bidverse_backend.Exceptions.NotificationAuctionNotFoundException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.services.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
class NotificationController(private val notificationService: NotificationService) {

    @PostMapping
    fun createNotification(@RequestBody notificationDTO: NotificationDTO): ResponseEntity<Any> {
        return try {
            val notification = notificationService.createNotification(notificationDTO)
            ResponseEntity.status(HttpStatus.CREATED).body(notification)
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: NotificationAuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating notification: ${e.message}")
        }
    }

    @GetMapping("/me")
    fun getAllNotificationsByUser(): ResponseEntity<Any> {
        return try {
            val notifications = notificationService.getAllNotificationsByUser()
            ResponseEntity.ok(notifications)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching notifications: ${e.message}")
        }
    }

    @GetMapping("/{id}")
    fun getNotificationById(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val notification = notificationService.getNotificationById(id)
            ResponseEntity.ok(notification)
        } catch (e: NotificationNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: NotificationPermissionDeniedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching notification: ${e.message}")
        }
    }

    @DeleteMapping("/{id}")
    fun deleteNotification(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            notificationService.deleteNotification(id)
            ResponseEntity.noContent().build()
        } catch (e: NotificationNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: NotificationPermissionDeniedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting notification: ${e.message}")
        }
    }
}
