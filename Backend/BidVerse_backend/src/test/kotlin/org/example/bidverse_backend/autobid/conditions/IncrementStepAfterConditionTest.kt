package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IncrementStepAfterConditionTest {

    private val condition = IncrementStepAfterCondition()

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
        val config = mapOf("5000" to 500, "10000" to 1000)
        
        assertNull(condition.modifyBidAmount(context, config, BigDecimal("1100.00")))
    }

    @Test
    fun `should apply 500 increment when price above 5000`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("6000.00")
        )
        val config = mapOf("5000" to 500)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 6000 + 500 = 6500
        assertNotNull(result)
        assertEquals(0, BigDecimal("6500").compareTo(result))
    }

    @Test
    fun `should apply 1000 increment when price above 10000`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("12000.00")
        )
        val config = mapOf("5000" to 500, "10000" to 1000)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 12000 + 1000 = 13000
        assertNotNull(result)
        assertEquals(0, BigDecimal("13000").compareTo(result))
    }

    @Test
    fun `should select highest matching threshold`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("50000.00")
        )
        val config = mapOf("10000" to 500, "30000" to 2000, "50000" to 5000)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 50000 + 5000 = 55000
        assertNotNull(result)
        assertEquals(0, BigDecimal("55000").compareTo(result))
    }

    @Test
    fun `should handle price exactly at threshold`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("20000.00")
        )
        val config = mapOf("20000" to 1500)
        
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        
        // 20000 + 1500 = 21500
        assertNotNull(result)
        assertEquals(0, BigDecimal("21500").compareTo(result))
    }

    @Test
    fun `shouldBid always returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, mapOf("1000" to 100)))
    }
}
