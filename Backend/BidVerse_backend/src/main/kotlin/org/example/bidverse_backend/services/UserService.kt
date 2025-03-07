package org.example.bidverse_backend.services

import org.example.bidverse_backend.Exceptions.AuthenticationException
import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.Exceptions.PermissionDeniedException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun updateUserContact(userBasic: UserBasicDTO): User {
        // Megkeressük a bejelentkezett felhasználót
        val user = userRepository.findById(getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        // Frissítjük a felhasználó adatait
        user.userName = userBasic.userName
        user.emailAddress = userBasic.emailAddress
        user.phoneNumber = userBasic.phoneNumber

        return userRepository.save(user)
    }
    fun deleteUserAsAdmin(userId: Int) {
        val adminUser = userRepository.findById(getCurrentUserId()).orElseThrow {
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
        val user = userRepository.findById(getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }

        userRepository.delete(user)
    }

    fun getUserProfile(): User {
        // Megkeressük a bejelentkezett felhasználót
        return userRepository.findById(getCurrentUserId())
            .orElseThrow { UserNotFoundException("User not found.") }
    }
/*
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

        return user
    }
*/
    fun getCurrentUserId(): Int {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is User) { // Tételezzük fel, hogy a `User` entitás implementálja a `UserDetails`-t
                return principal.id
            }
        }
        throw AuthenticationException("User not authenticated.")
    }
}