package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IncrementPercentageAfterConditionTest {

    private val condition = IncrementPercentageAfterCondition()

    @Test
    fun `should not modify when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `should not modify when no threshold matches`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        val config = mapOf("5000" to 0.05, "10000" to 0.10)
        
        assertNull(condition.modifyBidAmount(context, config, BigDecimal("1100.00")))
    }

    @Test
    fun `should apply 5 percent increment when price above 5000`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("6000.00")
        )
        val config = mapOf("5000" to 0.05)
        
        // baseAmount is ignored, uses currentPrice from context
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 6000 + (6000 * 0.05) = 6000 + 300 = 6300
        assertNotNull(result)
        assertEquals(0, BigDecimal("6300").compareTo(result))
    }

    @Test
    fun `should apply 10 percent increment when price above 10000`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("12000.00")
        )
        val config = mapOf("5000" to 0.05, "10000" to 0.10)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 12000 + (12000 * 0.10) = 12000 + 1200 = 13200
        assertNotNull(result)
        assertEquals(0, BigDecimal("13200").compareTo(result))
    }

    @Test
    fun `should select highest matching threshold`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("50000.00")
        )
        val config = mapOf("10000" to 0.05, "30000" to 0.08, "50000" to 0.12)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 50000 + (50000 * 0.12) = 50000 + 6000 = 56000
        assertNotNull(result)
        assertEquals(0, BigDecimal("56000").compareTo(result))
    }

    @Test
    fun `should handle price exactly at threshold`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("20000.00")
        )
        val config = mapOf("20000" to 0.07)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 20000 + (20000 * 0.07) = 20000 + 1400 = 21400
        assertNotNull(result)
        assertEquals(0, BigDecimal("21400").compareTo(result))
    }

    @Test
    fun `should round result to nearest integer`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1234.00")
        )
        val config = mapOf("1000" to 0.055)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 1234 + (1234 * 0.055) = 1234 + 67.87 = 1301.87 → rounds to 1302
        assertNotNull(result)
        assertEquals(0, BigDecimal("1302").compareTo(result))
    }

    @Test
    fun `should handle single threshold configuration`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("8000.00")
        )
        val config = mapOf("5000" to 0.03)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 8000 + (8000 * 0.03) = 8000 + 240 = 8240
        assertNotNull(result)
        assertEquals(0, BigDecimal("8240").compareTo(result))
    }

    @Test
    fun `shouldBid always returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, mapOf("1000" to 0.05)))
    }
}
