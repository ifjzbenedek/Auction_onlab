package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ActiveHoursConditionTest {

    private val condition = ActiveHoursCondition()

    @Test
    fun `null config allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `empty list blocks bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertFalse(condition.shouldBid(context, emptyList<Int>()))
    }

    @Test
    fun `bids during active hours`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 30)
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertTrue(condition.shouldBid(context, listOf(9, 10, 14, 15, 18)))
        assertFalse(condition.shouldBid(context, listOf(9, 10, 11, 12, 13)))
    }

    @Test
    fun `handles edge hours`() {
        val midnight = LocalDateTime.of(2025, 12, 7, 0, 30)
        val contextMidnight = ConditionTestHelpers.createContextWithExactTime(
            currentTime = midnight,
            expiredDate = midnight.plusHours(2)
        )
        assertTrue(condition.shouldBid(contextMidnight, listOf(0, 1, 2)))
        
        val evening = LocalDateTime.of(2025, 12, 7, 23, 45)
        val contextEvening = ConditionTestHelpers.createContextWithExactTime(
            currentTime = evening,
            expiredDate = evening.plusHours(2)
        )
        assertTrue(condition.shouldBid(contextEvening, listOf(22, 23)))
    }

    @Test
    fun `business hours work correctly`() {
        val now = LocalDateTime.of(2025, 12, 7, 16, 30)
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        val businessHours = listOf(9, 10, 11, 12, 13, 14, 15, 16, 17)
        assertTrue(condition.shouldBid(context, businessHours))
    }
}
