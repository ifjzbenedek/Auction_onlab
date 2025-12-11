package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OnlyIfPriceAboveConditionTest {

    private val condition = OnlyIfPriceAboveCondition()

    @Test
    fun `null config allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `bids when price above or equal to minimum`() {
        val context1000 = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        assertTrue(condition.shouldBid(context1000, 500))
        assertTrue(condition.shouldBid(context1000, 1000))
        
        val context500 = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("500.00"))
        assertFalse(condition.shouldBid(context500, 1000))
    }

    @Test
    fun `handles different price thresholds`() {
        val contextLow = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("100.00"))
        assertTrue(condition.shouldBid(contextLow, 50))
        
        val contextHigh = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("5000.00"))
        assertFalse(condition.shouldBid(contextHigh, 10000))
    }
}
