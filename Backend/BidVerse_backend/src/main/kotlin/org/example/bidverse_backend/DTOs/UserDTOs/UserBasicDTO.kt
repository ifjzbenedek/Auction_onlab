package org.example.bidverse_backend.DTOs.UserDTOs

data class UserBasicDTO(
    val id: Int,
    val userName: String,
    val emailAddress: String,
    val phoneNumber: String
)