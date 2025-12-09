package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MaxIncrementConditionTest {

    private val condition = MaxIncrementCondition()

    @Test
    fun `should not modify when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `should not modify when increment is within limit`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        // baseAmount 1050, currentPrice 1000 → increment 50, max 100 → OK
        assertNull(condition.modifyBidAmount(context, 100, BigDecimal("1050.00")))
    }

    @Test
    fun `should cap increment when it exceeds max`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        // baseAmount 1200, currentPrice 1000 → increment 200, max 100 → cap to 1100
        val result = condition.modifyBidAmount(context, 100, BigDecimal("1200.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("1100").compareTo(result))
    }

    @Test
    fun `should cap large increment`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("5000.00")
        )
        
        // baseAmount 8000, currentPrice 5000 → increment 3000, max 500 → cap to 5500
        val result = condition.modifyBidAmount(context, 500, BigDecimal("8000.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("5500").compareTo(result))
    }

    @Test
    fun `should allow increment equal to max`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        // baseAmount 1100, currentPrice 1000 → increment 100, max 100 → OK
        assertNull(condition.modifyBidAmount(context, 100, BigDecimal("1100.00")))
    }

    @Test
    fun `should handle small max increment`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("10000.00")
        )
        
        // baseAmount 10500, currentPrice 10000 → increment 500, max 50 → cap to 10050
        val result = condition.modifyBidAmount(context, 50, BigDecimal("10500.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("10050").compareTo(result))
    }

    @Test
    fun `shouldBid always returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, 100))
    }
}
