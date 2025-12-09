package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MinIncrementConditionTest {

    private val condition = MinIncrementCondition()

    @Test
    fun `should not modify when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `should return null when base is already above minimum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        // baseAmount 1100, currentPrice 1000, minIncrement 50 → min would be 1050, but base is 1100
        // No modification needed
        assertNull(condition.modifyBidAmount(context, 50, BigDecimal("1100.00")))
    }

    @Test
    fun `should enforce minimum when base is below minimum`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        // baseAmount 1020, currentPrice 1000, minIncrement 50 → min 1050 > 1020, enforce min
        val result = condition.modifyBidAmount(context, 50, BigDecimal("1020.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("1050").compareTo(result))
    }

    @Test
    fun `should handle large minimum increment`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        // baseAmount 1100, currentPrice 1000, minIncrement 200 → min 1200 > 1100, enforce min
        val result = condition.modifyBidAmount(context, 200, BigDecimal("1100.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("1200").compareTo(result))
    }

    @Test
    fun `should return null when small minimum already satisfied`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        // baseAmount 1050, currentPrice 1000, minIncrement 10 → min 1010 < 1050, no change
        assertNull(condition.modifyBidAmount(context, 10, BigDecimal("1050.00")))
    }

    @Test
    fun `should enforce minimum when base equals current price`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        // baseAmount 1000, currentPrice 1000, minIncrement 50 → min 1050 > 1000, enforce min
        val result = condition.modifyBidAmount(context, 50, BigDecimal("1000.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("1050").compareTo(result))
    }

    @Test
    fun `shouldBid always returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, 50))
    }
}
