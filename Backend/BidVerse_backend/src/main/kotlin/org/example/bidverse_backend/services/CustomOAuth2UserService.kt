package org.example.bidverse_backend.services

import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : OidcUserService() {
    
    private val logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)

    init {
        println("========================================")
        println("CustomOAuth2UserService INITIALIZED!")
        println("========================================")
        logger.warn("CustomOAuth2UserService INITIALIZED!")
    }

    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        logger.warn("!!! CustomOAuth2UserService.loadUser() CALLED !!!")
        
        val oidcUser = super.loadUser(userRequest)

        val email = oidcUser.email
        val name = oidcUser.fullName
        
        logger.info("=== OAuth2 Login Attempt ===")
        logger.info("Email: $email")
        logger.info("Name: $name")
        
        if (email == null) {
            logger.error("Email not found in OAuth2 response!")
            throw IllegalArgumentException("Email not found.")
        }
        
        val userName = name ?: email.substringBefore("@")

        // Check if user exists
        var existingUser = userRepository.findByEmailAddress(email)
        
        if (existingUser != null) {
            logger.info("Existing user found: ID=${existingUser.id}, Username=${existingUser.userName}")
            // Update username if changed
            if (existingUser.userName != userName) {
                existingUser.userName = userName
                userRepository.save(existingUser)
                logger.info("User updated successfully: ID=${existingUser.id}")
            }
            return oidcUser
        }

        logger.info("No existing user found. Creating new user with email: $email, username: $userName")
        
        // Create new user
        try {
            val newUser = User(
                id = null,
                auctions = mutableListOf(),
                bids = mutableListOf(),
                watches = mutableListOf(),
                uploadedImages = mutableListOf(),
                userName = userName,
                emailAddress = email,
                phoneNumber = "",
                role = "USER"
            )
            
            logger.info("Attempting to save new user...")
            val savedUser = userRepository.save(newUser)
            userRepository.flush()
            
            logger.info(" User saved successfully: ID=${savedUser.id}, Email=${savedUser.emailAddress}")
            
        } catch (e: Exception) {
            logger.error(" Failed to create user!", e)
            logger.error("Exception: ${e.javaClass.name} - ${e.message}")
            throw e
        }
        
        return oidcUser
    }
}