package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>