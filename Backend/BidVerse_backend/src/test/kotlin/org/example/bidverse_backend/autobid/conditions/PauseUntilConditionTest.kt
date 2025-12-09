package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PauseUntilConditionTest {

    private val condition = PauseUntilCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when current time is after pause time`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 30)
        val pauseUntil = LocalDateTime.of(2025, 12, 7, 10, 0)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertTrue(condition.shouldBid(context, pauseUntil.toString()))
    }

    @Test
    fun `should return false when current time is before pause time`() {
        val now = LocalDateTime.of(2025, 12, 7, 8, 30)
        val pauseUntil = LocalDateTime.of(2025, 12, 7, 10, 0)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertFalse(condition.shouldBid(context, pauseUntil.toString()))
    }

    @Test
    fun `should return false when current time equals pause time`() {
        val now = LocalDateTime.of(2025, 12, 7, 10, 0)
        val pauseUntil = LocalDateTime.of(2025, 12, 7, 10, 0)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        // isAfter returns false when equal
        assertFalse(condition.shouldBid(context, pauseUntil.toString()))
    }

    @Test
    fun `should return true for invalid datetime format`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        // Invalid format should be ignored (return true)
        assertTrue(condition.shouldBid(context, "invalid-datetime"))
    }

    @Test
    fun `should handle pause overnight`() {
        val now = LocalDateTime.of(2025, 12, 8, 1, 0) // 1 AM next day
        val pauseUntil = LocalDateTime.of(2025, 12, 7, 23, 0) // 11 PM previous day
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        // Now is after pause time → can bid
        assertTrue(condition.shouldBid(context, pauseUntil.toString()))
    }

    @Test
    fun `should handle pause until future date`() {
        val now = LocalDateTime.of(2025, 12, 7, 10, 0)
        val pauseUntil = LocalDateTime.of(2025, 12, 10, 10, 0) // 3 days later
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertFalse(condition.shouldBid(context, pauseUntil.toString()))
    }
}
