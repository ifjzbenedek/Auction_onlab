package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AvoidRoundNumbersConditionTest {

    private val condition = AvoidRoundNumbersCondition()

    @Test
    fun `disabled condition does nothing`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("1000.00")))
        assertNull(condition.modifyBidAmount(context, false, BigDecimal("1000.00")))
    }

    @Test
    fun `non-round numbers unchanged`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, true, BigDecimal("1234.00")))
        assertNull(condition.modifyBidAmount(context, true, BigDecimal("150.00")))
    }

    @Test
    fun `modifies round hundreds`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val baseAmount = BigDecimal("1000.00")
        
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        assertNotNull(result)
        assertTrue(result!! > baseAmount)
        assertTrue(result in BigDecimal("1007")..BigDecimal("1047"))
    }

    @Test
    fun `works with different round numbers`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        val result500 = condition.modifyBidAmount(context, true, BigDecimal("500.00"))
        assertNotNull(result500)
        assertTrue(result500!! in BigDecimal("507")..BigDecimal("547"))
        
        val result10k = condition.modifyBidAmount(context, true, BigDecimal("10000.00"))
        assertNotNull(result10k)
        assertTrue(result10k!! in BigDecimal("10007")..BigDecimal("10047"))
    }

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, true))
    }
}
