package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserRegistrationDTO
import org.example.bidverse_backend.Exceptions.PermissionDeniedException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.Security.SecurityUtils
import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository,
                  private val securityUtils: SecurityUtils
) {

    fun updateUserContact(userBasic: UserBasicDTO): User {
        // Megkeressük a bejelentkezett felhasználót
        val user = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        // Frissítjük a felhasználó adatait
        user.userName = userBasic.userName
        user.emailAddress = userBasic.emailAddress
        user.phoneNumber = userBasic.phoneNumber

        return userRepository.save(user)
    }
    fun deleteUserAsAdmin(userId: Int) {
        val adminUser = userRepository.findById(securityUtils.getCurrentUserId()).orElseThrow {
            UserNotFoundException("Admin user not found.")
        }

        if (adminUser.role != "ADMIN") {
            throw PermissionDeniedException("You do not have permission to delete this user.")
        }

        val user = userRepository.findById(userId).orElseThrow {
            UserNotFoundException("User not found.")
        }
        userRepository.delete(user)
    }

    fun deleteUser() {
        val user = userRepository.findById(securityUtils.getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        userRepository.delete(user)
    }

    fun getUserProfile(): User {
        val userId = securityUtils.getCurrentUserId()
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found.") }
    }

    fun register(userRegistrationDTO: UserRegistrationDTO): User {
        // Ellenőrizzük, hogy a jelszavak megegyeznek-e
        /*
        require(userRegistrationDTO.password == userRegistrationDTO.rePassword) {
            throw IllegalArgumentException("Passwords don't match.")
        }*/

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
            auctions = mutableListOf(),
            bids = mutableListOf(),
            watches = mutableListOf()
        )

        return userRepository.save(user)
    }

}