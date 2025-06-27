package org.example.bidverse_backend.services

import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.example.bidverse_backend.entities.User
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService

@Service
class CustomOAuth2UserService(private val userRepository: UserRepository) : DefaultOAuth2UserService() {

    private val lock = Any() // Szinkronizációs objektum

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)

        val email = oauth2User.getAttribute<String>("email") ?: throw IllegalArgumentException("Email not found.")
        val userName = oauth2User.getAttribute<String>("name") ?: email.substringBefore("@")

        synchronized(lock) { // Szinkronizálás
            val existingUser = userRepository.findByEmailAddress(email)
            if (existingUser != null) {
                existingUser.userName = userName
                userRepository.save(existingUser)
                return oauth2User
            }

            val newUser = User(
                userName = userName,
                emailAddress = email,
                phoneNumber = "",
                auctions = mutableListOf(),
                bids = mutableListOf(),
                watches = mutableListOf()
            )

            userRepository.save(newUser)
            return oauth2User
        }
    }
}