package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IncrementRelativeToPriceConditionTest {

    private val condition = IncrementRelativeToPriceCondition()

    @Test
    fun `should not modify when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `should apply 5 percent increment`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.05, BigDecimal("999.00"))
        
        // 1000 + (1000 * 0.05) = 1000 + 50 = 1050
        assertNotNull(result)
        assertEquals(0, BigDecimal("1050").compareTo(result))
    }

    @Test
    fun `should apply 10 percent increment`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("5000.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.10, BigDecimal("999.00"))
        
        // 5000 + (5000 * 0.10) = 5000 + 500 = 5500
        assertNotNull(result)
        assertEquals(0, BigDecimal("5500").compareTo(result))
    }

    @Test
    fun `should apply 3 percent increment`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("20000.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.03, BigDecimal("999.00"))
        
        // 20000 + (20000 * 0.03) = 20000 + 600 = 20600
        assertNotNull(result)
        assertEquals(0, BigDecimal("20600").compareTo(result))
    }

    @Test
    fun `should round result to nearest integer`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1234.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.055, BigDecimal("999.00"))
        
        // 1234 + (1234 * 0.055) = 1234 + 67.87 = 1301.87 → 1302
        assertNotNull(result)
        assertEquals(0, BigDecimal("1302").compareTo(result))
    }

    @Test
    fun `should handle small percentage (1 percent)`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("10000.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.01, BigDecimal("999.00"))
        
        // 10000 + (10000 * 0.01) = 10000 + 100 = 10100
        assertNotNull(result)
        assertEquals(0, BigDecimal("10100").compareTo(result))
    }

    @Test
    fun `should handle large percentage (25 percent)`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("800.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.25, BigDecimal("999.00"))
        
        // 800 + (800 * 0.25) = 800 + 200 = 1000
        assertNotNull(result)
        assertEquals(0, BigDecimal("1000").compareTo(result))
    }

    @Test
    fun `shouldBid always returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, 0.05))
    }
}
