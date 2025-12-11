package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MaxIncrementConditionTest {

    private val condition = MaxIncrementCondition()

    @Test
    fun `null config does nothing`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `does not modify when increment is within limit`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        assertNull(condition.modifyBidAmount(context, 100, BigDecimal("1050.00")))
        assertNull(condition.modifyBidAmount(context, 100, BigDecimal("1100.00")))
    }

    @Test
    fun `caps increment when it exceeds max`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        
        val result = condition.modifyBidAmount(context, 100, BigDecimal("1200.00"))
        assertEquals(0, BigDecimal("1100").compareTo(result))
    }

    @Test
    fun `handles large increments`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("5000.00"))
        
        // trying to bid 8000, but max increment is 500, so should cap at 5500
        val result = condition.modifyBidAmount(context, 500, BigDecimal("8000.00"))
        assertEquals(0, BigDecimal("5500").compareTo(result))
    }

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, 100))
    }
}
