package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserCredentialsDTO
import org.example.bidverse_backend.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PutMapping("/me")
    fun updateUserContact(@RequestBody userBasic: UserBasicDTO): UserBasicDTO {
        val user = userService.updateUserContact(userBasic)
        return user.toUserBasic() // DTO-ra konvertálás
    }

    @DeleteMapping("/me")
    fun deleteUser() {
        userService.deleteUser()
    }

    @GetMapping("/me")
    fun getUserProfile(): UserBasic {
        val user = userService.getUserProfile()
        return user.toUserBasic() // DTO-ra konvertálás
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody userCredentials: UserCredentialsDTO): UserBasicDTO {
        val user = userService.createUser(userCredentials)
        return user.toUserBasic() // DTO-ra konvertálás
    }

    @PostMapping("/login")
    fun login(@RequestBody userCredentials: UserCredentialsDTO): Map<String, String> {
        val message = userService.login(userCredentials)
        return mapOf("message" to message)
    }
}