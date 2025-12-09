package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class ActiveHoursConditionTest {

    private val condition = ActiveHoursCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return false when condition is empty list (no active hours means never bid)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertFalse(condition.shouldBid(context, emptyList<Int>()))
    }

    @Test
    fun `should return true when current hour is in active hours`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 30) // 14:30
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertTrue(condition.shouldBid(context, listOf(9, 10, 14, 15, 18)))
    }

    @Test
    fun `should return false when current hour is not in active hours`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 30) // 14:30
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertFalse(condition.shouldBid(context, listOf(9, 10, 11, 12, 13)))
    }

    @Test
    fun `should work with single active hour`() {
        val now = LocalDateTime.of(2025, 12, 7, 9, 15) // 09:15
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertTrue(condition.shouldBid(context, listOf(9)))
    }

    @Test
    fun `should handle midnight hour (0)`() {
        val now = LocalDateTime.of(2025, 12, 7, 0, 30) // 00:30
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertTrue(condition.shouldBid(context, listOf(0, 1, 2)))
    }

    @Test
    fun `should handle late evening hour (23)`() {
        val now = LocalDateTime.of(2025, 12, 7, 23, 45) // 23:45
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertTrue(condition.shouldBid(context, listOf(22, 23)))
    }

    @Test
    fun `should return false for early morning when only afternoon configured`() {
        val now = LocalDateTime.of(2025, 12, 7, 6, 0) // 06:00
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertFalse(condition.shouldBid(context, listOf(14, 15, 16, 17, 18, 19)))
    }

    @Test
    fun `should handle business hours configuration`() {
        val now = LocalDateTime.of(2025, 12, 7, 16, 30) // 16:30
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        val businessHours = listOf(9, 10, 11, 12, 13, 14, 15, 16, 17)
        assertTrue(condition.shouldBid(context, businessHours))
    }
}
