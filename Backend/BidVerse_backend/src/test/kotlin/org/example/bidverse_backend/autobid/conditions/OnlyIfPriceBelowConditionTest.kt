package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OnlyIfPriceBelowConditionTest {

    private val condition = OnlyIfPriceBelowCondition()

    @Test
    fun `null config allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `bids only when price below maximum`() {
        val context500 = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("500.00"))
        assertTrue(condition.shouldBid(context500, 1000))
        
        val context1000 = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        assertFalse(condition.shouldBid(context1000, 1000))
        
        val context1500 = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1500.00"))
        assertFalse(condition.shouldBid(context1500, 1000))
    }

    @Test
    fun `works with different price caps`() {
        val contextLow = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("150.00"))
        assertFalse(condition.shouldBid(contextLow, 100))
        
        val contextHigh = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("5000.00"))
        assertTrue(condition.shouldBid(contextHigh, 10000))
    }
}
