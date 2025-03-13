package org.example.bidverse_backend.Security

import org.example.bidverse_backend.Exceptions.AuthenticationException
import org.example.bidverse_backend.entities.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder


object SecurityUtils {
    fun getCurrentUserId(): Int {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is User) {
                return principal.id!!
            }
        }
        throw AuthenticationException("User no authenticated.")
    }
}