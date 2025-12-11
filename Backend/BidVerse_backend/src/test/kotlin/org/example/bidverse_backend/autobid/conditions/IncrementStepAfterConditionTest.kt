package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IncrementStepAfterConditionTest {

    private val condition = IncrementStepAfterCondition()

    @Test
    fun `null config does nothing`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `no change when no threshold matches`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        val config = mapOf("5000" to 500, "10000" to 1000)
        assertNull(condition.modifyBidAmount(context, config, BigDecimal("1100.00")))
    }

    @Test
    fun `applies increment based on price thresholds`() {
        val context5k = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("6000.00"))
        val result5k = condition.modifyBidAmount(context5k, mapOf("5000" to 500), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("6500").compareTo(result5k))
        
        val context10k = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("12000.00"))
        val result10k = condition.modifyBidAmount(context10k, mapOf("5000" to 500, "10000" to 1000), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("13000").compareTo(result10k))
    }

    @Test
    fun `selects highest matching threshold`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("50000.00"))
        val config = mapOf("10000" to 500, "30000" to 2000, "50000" to 5000)
        val result = condition.modifyBidAmount(context, config, BigDecimal("999.00"))
        assertEquals(0, BigDecimal("55000").compareTo(result))
    }

    @Test
    fun `works at exact threshold`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("20000.00"))
        val result = condition.modifyBidAmount(context, mapOf("20000" to 1500), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("21500").compareTo(result))
    }

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, mapOf("1000" to 100)))
    }
}
