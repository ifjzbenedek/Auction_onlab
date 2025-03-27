package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.CategoryDTOs.CategoryDTO
import org.example.bidverse_backend.entities.Category
import org.example.bidverse_backend.repositories.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getAllCategories(): List<CategoryDTO> {
        return categoryRepository.findAll().map { CategoryDTO(it.id ?: 0, it.categoryName ?: "") }
    }
}