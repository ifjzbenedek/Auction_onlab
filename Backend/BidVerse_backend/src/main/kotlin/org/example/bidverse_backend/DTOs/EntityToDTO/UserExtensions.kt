package org.example.bidverse_backend.extensions

import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserCredentialsDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserRegistrationDTO
import org.example.bidverse_backend.entities.User

fun User.toUserBasicDTO(): UserBasicDTO {
    return UserBasicDTO(
        id = this.id,
        userName = this.userName,
        emailAddress = this.emailAddress,
        phoneNumber = this.phoneNumber
    )
}

fun User.toUserCredentialsDTO(): UserCredentialsDTO {
    return UserCredentialsDTO(
        userName = this.userName,
    )
}

fun User.toUserRegistrationDTO(): UserRegistrationDTO {
    return UserRegistrationDTO(
        userName = this.userName,
        emailAddress = this.emailAddress,

    )
}