package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>{

    fun findByUserName(userName: String): User?

    fun findByEmailAddress(email: String): User?

    fun findByPhoneNumber(phone: String): User?

    fun existsByUserName(userName: String): Boolean

    fun existsByEmailAddress(email: String): Boolean
}