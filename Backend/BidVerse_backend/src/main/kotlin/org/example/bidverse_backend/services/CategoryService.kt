package org.example.bidverse_backend.services

import org.example.bidverse_backend.entities.Category
import org.example.bidverse_backend.repositories.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }
}