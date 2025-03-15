package org.example.bidverse_backend.Security

import org.example.bidverse_backend.Exceptions.AuthenticationException
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class SecurityUtils(private val userRepository: UserRepository) {

    fun getCurrentUserId(): Int {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is OAuth2User) {
                val email = principal.attributes["email"] as? String
                if (email != null) {
                    val user = userRepository.findByEmailAddress(email)
                        ?: throw AuthenticationException("User not found.")
                    return user.id!!
                }
            }
        }
        throw AuthenticationException("User not authenticated.")
    }
}