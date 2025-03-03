package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserCredentialsDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserRegistrationDTO
import org.example.bidverse_backend.Global
import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.extensions.toUserBasicDTO
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun updateUserContact(userBasic: UserBasicDTO): User {
        // Megkeressük a bejelentkezett felhasználót
        val user = userRepository.findById(Global.loggedInUserId)
            .orElseThrow { IllegalArgumentException("User not found.") }

        // Frissítjük a felhasználó adatait
        user.userName = userBasic.userName
        user.emailAddress = userBasic.emailAddress
        user.phoneNumber = userBasic.phoneNumber

        return userRepository.save(user)
    }
    fun deleteUserAsAdmin(userId: Int) {
        val adminUser = userRepository.findById(Global.loggedInUserId).orElseThrow {
            IllegalArgumentException("Current user not found.")
        }

        if (adminUser.role != "ADMIN") {
            throw SecurityException("You do not have permission to delete this user.")
        }

        val user = userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("User not found.")
        }
        userRepository.delete(user)
    }
    fun deleteUser() {
        val user = userRepository.findById(Global.loggedInUserId)
            .orElseThrow { IllegalArgumentException("User not found.") }

        userRepository.delete(user)
    }

    fun getUserProfile(): User {
        // Megkeressük a bejelentkezett felhasználót
        return userRepository.findById(Global.loggedInUserId)
            .orElseThrow { IllegalArgumentException("User not found.") }
    }

    fun register(userRegistrationDTO: UserRegistrationDTO): User {
        // Ellenőrizzük, hogy a jelszavak megegyeznek-e
        require(userRegistrationDTO.password == userRegistrationDTO.rePassword) {
            throw IllegalArgumentException("Passwords don't match.")
        }

        // Ellenőrizzük, hogy az email vagy a felhasználónév már foglalt-e
        require(!userRepository.existsByEmailAddress(userRegistrationDTO.emailAddress)){
                throw IllegalArgumentException("Email address already in use.")}

        require (!userRepository.existsByUserName(userRegistrationDTO.userName)) {
            throw IllegalArgumentException("Username already in use.")
        }

        val user = User(
            userName = userRegistrationDTO.userName,
            emailAddress = userRegistrationDTO.emailAddress,
            phoneNumber = "", // Opcionális érték
            passwordHash = userRegistrationDTO.password ,
            auctions = mutableListOf(),
            bids = mutableListOf(),
            watches = mutableListOf()
        )

        return userRepository.save(user)
    }

    fun login(userCredentials: UserCredentialsDTO): User {

        // Előkeressük a megfelelő felhasználót
        val user = userRepository.findByUserName(userCredentials.userName)
            ?: throw IllegalArgumentException("User not found.")

        // Ellenőrizzük a jelszót, hogy megegyezik-e a tárolttal
        // Egyelőre még csak átmeneti, nincs átalakítás
        require (user.passwordHash == userCredentials.password) {
            throw IllegalArgumentException("Invalid password.")
        }

        return user
    }
}