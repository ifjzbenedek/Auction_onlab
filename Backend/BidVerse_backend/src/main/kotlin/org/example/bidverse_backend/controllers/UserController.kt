package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserCredentialsDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserRegistrationDTO
import org.example.bidverse_backend.extensions.toUserBasicDTO
import org.example.bidverse_backend.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PutMapping("/me")
    fun updateUserContact(@RequestBody userBasic: UserBasicDTO): ResponseEntity<UserBasicDTO> {
        val user = userService.updateUserContact(userBasic)
        return ResponseEntity.ok(user.toUserBasicDTO()) // `200 OK`
    }

    @DeleteMapping("/me")
    fun deleteUser(): ResponseEntity<Unit> {
        userService.deleteUser()
        return ResponseEntity.noContent().build() // `204 No Content`, nincs visszatérő adat
    }

    @GetMapping("/me")
    fun getUserProfile(): ResponseEntity<UserBasicDTO> {
        val user = userService.getUserProfile()
        return ResponseEntity.ok(user.toUserBasicDTO()) // `200 OK`
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody userCredentials: UserCredentialsDTO): ResponseEntity<UserBasicDTO> {
        val user = userService.login(userCredentials)
        return ResponseEntity.ok(user.toUserBasicDTO()) // `200 OK`
    }

    @PostMapping("/register")
    fun register(@RequestBody userRegistrationDTO: UserRegistrationDTO): ResponseEntity<Any> {
        return try {
            val user = userService.register(userRegistrationDTO)
            ResponseEntity.status(HttpStatus.CREATED).body(user.toUserBasicDTO())
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Hiba történt a regisztráció során.")
        }
    }
}