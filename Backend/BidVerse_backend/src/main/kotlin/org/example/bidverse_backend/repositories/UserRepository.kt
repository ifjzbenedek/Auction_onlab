package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>