package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Int>{
    fun findByIdIn(categoryIds: List<Int>): List<Category>

    fun findByCategoryName(categoryName: String): Category?

    fun findByCategoryNameIn(categoryNames: List<String>): List<Category>
}