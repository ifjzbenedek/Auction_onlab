package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OnlyIfPriceBelowConditionTest {

    private val condition = OnlyIfPriceBelowCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when price is below maximum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("500.00")
        )
        
        assertTrue(condition.shouldBid(context, 1000))
    }

    @Test
    fun `should return false when price equals maximum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        assertFalse(condition.shouldBid(context, 1000))
    }

    @Test
    fun `should return false when price is above maximum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1500.00")
        )
        
        assertFalse(condition.shouldBid(context, 1000))
    }

    @Test
    fun `should handle small maximum price`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("150.00")
        )
        
        assertFalse(condition.shouldBid(context, 100))
    }

    @Test
    fun `should handle large maximum price`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("5000.00")
        )
        
        assertTrue(condition.shouldBid(context, 10000))
    }
}
