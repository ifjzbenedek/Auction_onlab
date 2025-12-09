package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AvoidRoundNumbersConditionTest {

    private val condition = AvoidRoundNumbersCondition()

    @Test
    fun `should not modify when condition is disabled (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("1000.00")
        
        assertNull(condition.modifyBidAmount(context, null, baseAmount))
    }

    @Test
    fun `should not modify when condition is disabled (false)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("1000.00")
        
        assertNull(condition.modifyBidAmount(context, false, baseAmount))
    }

    @Test
    fun `should not modify when amount is not a round number`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("1234.00")
        
        assertNull(condition.modifyBidAmount(context, true, baseAmount))
    }

    @Test
    fun `should modify when amount is round hundred (1000)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("1000.00")
        
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        assertNotNull(result)
        assertTrue(result!! > baseAmount)
        // Should add one of the odd offsets (7, 11, 13, 17, 23, 29, 37, 47)
        assertTrue(result in BigDecimal("1007")..BigDecimal("1047"))
    }

    @Test
    fun `should modify when amount is round hundred (500)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("500.00")
        
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        assertNotNull(result)
        assertTrue(result!! > baseAmount)
        assertTrue(result in BigDecimal("507")..BigDecimal("547"))
    }

    @Test
    fun `should modify when amount is large round number (10000)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("10000.00")
        
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        assertNotNull(result)
        assertTrue(result!! > baseAmount)
        assertTrue(result in BigDecimal("10007")..BigDecimal("10047"))
    }

    @Test
    fun `should not modify 150 (not divisible by 100)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("150.00")
        
        assertNull(condition.modifyBidAmount(context, true, baseAmount))
    }

    @Test
    fun `should not modify 250 (not divisible by 100)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("250.00")
        
        assertNull(condition.modifyBidAmount(context, true, baseAmount))
    }

    @Test
    fun `shouldBid always returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, false))
        assertTrue(condition.shouldBid(context, true))
    }
}
