package org.example.bidverse_backend.services

import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.example.bidverse_backend.entities.User
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oauth2User = delegate.loadUser(userRequest)

        // Az OAuth válaszából kinyerjük a felhasználó adatait
        val userName = oauth2User.getAttribute<String>("name") ?: ""
        val email = oauth2User.getAttribute<String>("email") ?: ""

        // Keresünk a rendszerben a felhasználót
        val existingUser = userRepository.findByEmailAddress(email)
        if (existingUser != null) {
            existingUser.userName = userName
            userRepository.save(existingUser)
            return oauth2User
        }

        // Ha nem találunk felhasználót, akkor létrehozunk egy új felhasználót
        val newUser = User(
            userName = userName,
            emailAddress = email,
            phoneNumber = "", // Ha szükséges, ide is hozzáadhatjuk
            auctions = mutableListOf(),
            bids = mutableListOf(),
            watches = mutableListOf()
        )

        userRepository.save(newUser)

        return oauth2User
    }
}