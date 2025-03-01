package org.example.bidverse_backend.DTOs.UserDTOs

data class UserRegistrationDTO(
    val userName: String,
    val password: String,
    val rePassword: String,
    val emailAddress: String
)