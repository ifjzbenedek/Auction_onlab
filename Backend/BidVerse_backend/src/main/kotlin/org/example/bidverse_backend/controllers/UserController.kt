package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.UserDTOs.UserBasicDTO
import org.example.bidverse_backend.DTOs.UserDTOs.UserRegistrationDTO
import org.example.bidverse_backend.Exceptions.PermissionDeniedException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.extensions.toUserBasicDTO
import org.example.bidverse_backend.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PutMapping("/me")
    fun updateUserContact(@RequestBody userBasic: UserBasicDTO): ResponseEntity<Any> {
        return try {
            val user = userService.updateUserContact(userBasic)
            ResponseEntity.ok(user.toUserBasicDTO()) // `200 OK`
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @DeleteMapping("/me")
    fun deleteUser(): ResponseEntity<Any> {
       return try{
              userService.deleteUser()
              ResponseEntity.noContent().build() // `204 No Content`
         } catch (e: UserNotFoundException) {
              ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
       }
   }
/*
    @PostMapping("/login")
    fun loginUser(@RequestBody userCredentials: UserCredentialsDTO): ResponseEntity<Any> {
        return try {
            val user = userService.login(userCredentials)
            ResponseEntity.ok(user.toUserBasicDTO()) // `200 OK`
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
*/

    @PostMapping("/register")
    fun register(@RequestBody userRegistrationDTO: UserRegistrationDTO): ResponseEntity<Any> {
        return try {
            val user = userService.register(userRegistrationDTO)
            ResponseEntity.status(HttpStatus.CREATED).body(user.toUserBasicDTO())
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error during registration!")
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUserByAdmin(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            userService.deleteUserAsAdmin(id)
            ResponseEntity.noContent().build() // 204 No Content
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: PermissionDeniedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        }
    }

    @GetMapping("/me")
    fun getUserProfile(): ResponseEntity<Any> {
        return try {
            val user = userService.getUserProfile()
            ResponseEntity.ok(user.toUserBasicDTO()) // `200 OK`
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

}