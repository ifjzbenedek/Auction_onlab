package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserCredentialsDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserRegistrationDTO
import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.extensions.toUserBasicDTO
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun updateUserContact(userBasic: UserBasicDTO): User {
        // Implementation to update user contact
    }

    fun deleteUser() {
        // Implementation to delete user
    }

    fun getUserProfile(): User {
        // Ez alapvetően implementálva van bele, ezért megtalálja
        // Ez csak teszteléshez kell most, később nem lesz beégetve az 1-es
        return userRepository.findById(1).get()
    }

    fun register(userRegistrationDTO: UserRegistrationDTO): User {
        if (userRegistrationDTO.password != userRegistrationDTO.rePassword) {
            throw IllegalArgumentException("Passwords don't match.")
        }

        // Ellenőrizzük, hogy az email vagy a felhasználónév már foglalt-e
        if (userRepository.existsByEmailAddress(userRegistrationDTO.emailAddress)
            throw IllegalArgumentException("Email address already in use.")

        if(userRepository.existsByUserName(userRegistrationDTO.userName))
            throw IllegalArgumentException("Username already in use.")


        val user = User(
            userName = userRegistrationDTO.userName,
            emailAddress = userRegistrationDTO.emailAddress,
            phoneNumber = "", // Opcionális érték
            passwordHash = userRegistrationDTO.password ,
            auctions = emptyList(),
            bids = emptyList(),
            watches = emptyList()
        )

        return userRepository.save(user)
    }

    fun login(userCredentials: UserCredentialsDTO): User {
        val user = userRepository.findByUserName(userCredentials.userName)
            ?: throw IllegalArgumentException("User not found.")

        if (user.passwordHash != userCredentials.password) {
            throw IllegalArgumentException("Invalid password.")
        }

        return user
    }
}