package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MinIncrementConditionTest {

    private val condition = MinIncrementCondition()

    @Test
    fun `null config does nothing`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `no change when base already above minimum`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        assertNull(condition.modifyBidAmount(context, 50, BigDecimal("1100.00")))
        assertNull(condition.modifyBidAmount(context, 10, BigDecimal("1050.00")))
    }

    @Test
    fun `enforces minimum when base is too low`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        
        val result = condition.modifyBidAmount(context, 50, BigDecimal("1020.00"))
        assertEquals(0, BigDecimal("1050").compareTo(result))
    }

    @Test
    fun `handles large minimum increments`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        
        val result = condition.modifyBidAmount(context, 200, BigDecimal("1100.00"))
        assertEquals(0, BigDecimal("1200").compareTo(result))
    }

    @Test
    fun `enforces minimum when base equals current`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        
        val result = condition.modifyBidAmount(context, 50, BigDecimal("1000.00"))
        assertEquals(0, BigDecimal("1050").compareTo(result))
    }

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, 50))
    }
}
