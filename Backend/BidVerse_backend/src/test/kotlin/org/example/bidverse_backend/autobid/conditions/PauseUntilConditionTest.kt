package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PauseUntilConditionTest {

    private val condition = PauseUntilCondition()

    @Test
    fun `null config allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `allows bidding after pause time`() {
        val now = LocalDateTime.of(2025, 12, 7, 14, 30)
        val pauseUntil = LocalDateTime.of(2025, 12, 7, 10, 0)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertTrue(condition.shouldBid(context, pauseUntil.toString()))
    }

    @Test
    fun `blocks bidding before pause time`() {
        val now = LocalDateTime.of(2025, 12, 7, 8, 30)
        val pauseUntil = LocalDateTime.of(2025, 12, 7, 10, 0)
        
        val context = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        
        assertFalse(condition.shouldBid(context, pauseUntil.toString()))
    }

    @Test
    fun `handles exact time and invalid formats`() {
        val now = LocalDateTime.of(2025, 12, 7, 10, 0)
        val contextExact = ConditionTestHelpers.createContextWithExactTime(
            currentTime = now,
            expiredDate = now.plusHours(2)
        )
        assertFalse(condition.shouldBid(contextExact, now.toString()))
        
        val contextInvalid = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(contextInvalid, "invalid-datetime"))
    }

    @Test
    fun `works across days`() {
        val nowNextDay = LocalDateTime.of(2025, 12, 8, 1, 0)
        val pauseLastNight = LocalDateTime.of(2025, 12, 7, 23, 0)
        
        val contextAfter = ConditionTestHelpers.createContextWithExactTime(
            currentTime = nowNextDay,
            expiredDate = nowNextDay.plusHours(2)
        )
        assertTrue(condition.shouldBid(contextAfter, pauseLastNight.toString()))
        
        val nowEarly = LocalDateTime.of(2025, 12, 7, 10, 0)
        val pauseFuture = LocalDateTime.of(2025, 12, 10, 10, 0)
        val contextBefore = ConditionTestHelpers.createContextWithExactTime(
            currentTime = nowEarly,
            expiredDate = nowEarly.plusHours(2)
        )
        assertFalse(condition.shouldBid(contextBefore, pauseFuture.toString()))
    }
}
