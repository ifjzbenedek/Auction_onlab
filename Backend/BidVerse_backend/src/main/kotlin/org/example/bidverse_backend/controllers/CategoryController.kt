package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.services.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<Any> {
        return try {
            val categories = categoryService.getAllCategories()
            ResponseEntity.ok(categories)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error") // 500 Internal Server Error
        }
    }
}