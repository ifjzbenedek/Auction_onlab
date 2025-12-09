package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class NearEndMinutesConditionTest {

    private val condition = NearEndMinutesCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when within threshold`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        val expiry = now.plusMinutes(5) // 5 minutes left
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = expiry
        )
        
        // 5 minutes left, threshold 10 → should bid
        assertTrue(condition.shouldBid(context, 10))
    }

    @Test
    fun `should return true when exactly at threshold`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        val expiry = now.plusMinutes(10) // 10 minutes left
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = expiry
        )
        
        // 10 minutes left, threshold 10 → should bid
        assertTrue(condition.shouldBid(context, 10))
    }

    @Test
    fun `should return false when beyond threshold`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        val expiry = now.plusMinutes(30) // 30 minutes left
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = expiry
        )
        
        // 30 minutes left, threshold 10 → should not bid yet
        assertFalse(condition.shouldBid(context, 10))
    }

    @Test
    fun `should return true when 1 minute left`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        val expiry = now.plusMinutes(1)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = expiry
        )
        
        assertTrue(condition.shouldBid(context, 5))
    }

    @Test
    fun `should return false when auction has long time left`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        val expiry = now.plusHours(2) // 120 minutes left
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = expiry
        )
        
        // 120 minutes left, threshold 10 → should not bid
        assertFalse(condition.shouldBid(context, 10))
    }

    @Test
    fun `should handle threshold of 0`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        val expiry = now.plusMinutes(1)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = expiry
        )
        
        // 1 minute left, threshold 0 → should not bid (must be exactly 0 or less)
        assertFalse(condition.shouldBid(context, 0))
    }

    @Test
    fun `should return true for very short threshold (1 minute)`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        val expiry = now.plusSeconds(30) // 0 minutes left (< 1 minute)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = expiry
        )
        
        // Less than 1 minute left, threshold 1 → should bid
        assertTrue(condition.shouldBid(context, 1))
    }
}
