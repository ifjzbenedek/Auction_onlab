package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OnlyIfPriceAboveConditionTest {

    private val condition = OnlyIfPriceAboveCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when price is above minimum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        assertTrue(condition.shouldBid(context, 500))
    }

    @Test
    fun `should return true when price equals minimum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        assertTrue(condition.shouldBid(context, 1000))
    }

    @Test
    fun `should return false when price is below minimum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("500.00")
        )
        
        assertFalse(condition.shouldBid(context, 1000))
    }

    @Test
    fun `should handle large minimum price`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("5000.00")
        )
        
        assertFalse(condition.shouldBid(context, 10000))
    }

    @Test
    fun `should handle small minimum price`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("100.00")
        )
        
        assertTrue(condition.shouldBid(context, 50))
    }
}
