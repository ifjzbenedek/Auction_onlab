package org.example.bidverse_backend.DTOs.EntityToDTO

import org.example.bidverse_backend.DTOs.CategoryDTOs.CategoryDTO
import org.example.bidverse_backend.entities.Category

fun Category.ToCategoryDTO(): CategoryDTO {
    return CategoryDTO(
        id = this.id!!,
        categoryName = this.categoryName
    )
}