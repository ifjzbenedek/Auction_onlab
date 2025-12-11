package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class NearEndMinutesConditionTest {

    private val condition = NearEndMinutesCondition()

    @Test
    fun `null config allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `bids when near auction end`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        
        val context5min = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusMinutes(5)
        )
        assertTrue(condition.shouldBid(context5min, 10))
        
        val context10min = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusMinutes(10)
        )
        assertTrue(condition.shouldBid(context10min, 10))
    }

    @Test
    fun `does not bid when too much time left`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        
        val context30min = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusMinutes(30)
        )
        assertFalse(condition.shouldBid(context30min, 10))
        
        val context2hours = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        assertFalse(condition.shouldBid(context2hours, 10))
    }

    @Test
    fun `handles edge cases`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 0)
        
        val context1min = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusMinutes(1)
        )
        assertTrue(condition.shouldBid(context1min, 5))
        assertFalse(condition.shouldBid(context1min, 0))
        
        val context30sec = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusSeconds(30)
        )
        assertTrue(condition.shouldBid(context30sec, 1))
    }
}
