package org.example.bidverse_backend.services

import org.springframework.stereotype.Service

@Service
class UserService {

    fun updateUserContact(userBasic: UserBasicDTO): User {
        // Implementation to update user contact
    }

    fun deleteUser() {
        // Implementation to delete user
    }

    fun getUserProfile(): User {
        // Implementation to get user profile
    }

    fun createUser(userCredentials: UserCredentialsDTO): User {
        // Implementation to create user
    }

    fun login(userCredentials: UserCredentialsDTO): String {
        // Implementation to login user
    }
}