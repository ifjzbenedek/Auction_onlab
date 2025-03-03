package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.entities.Watch
import org.springframework.data.jpa.repository.JpaRepository

interface WatchRepository : JpaRepository<Watch, Int> {
    fun findByUser(user: User): List<Watch>

}