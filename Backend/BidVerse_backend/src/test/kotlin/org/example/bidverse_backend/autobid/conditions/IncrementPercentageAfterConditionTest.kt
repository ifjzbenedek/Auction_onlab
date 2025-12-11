package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IncrementPercentageAfterConditionTest {

    private val condition = IncrementPercentageAfterCondition()

    @Test
    fun `null config does nothing`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `no change when no threshold matches`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        assertNull(condition.modifyBidAmount(context, mapOf("5000" to 0.05, "10000" to 0.10), BigDecimal("1100.00")))
    }

    @Test
    fun `applies percentage based on thresholds`() {
        val context5k = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("6000.00"))
        val result5k = condition.modifyBidAmount(context5k, mapOf("5000" to 0.05), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("6300").compareTo(result5k))
        
        val context10k = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("12000.00"))
        val result10k = condition.modifyBidAmount(context10k, mapOf("5000" to 0.05, "10000" to 0.10), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("13200").compareTo(result10k))
    }

    @Test
    fun `selects highest threshold`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("50000.00"))
        val result = condition.modifyBidAmount(context, mapOf("10000" to 0.05, "30000" to 0.08, "50000" to 0.12), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("56000").compareTo(result))
    }

    @Test
    fun `rounds result properly`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1234.00"))
        val result = condition.modifyBidAmount(context, mapOf("1000" to 0.055), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("1302").compareTo(result))
    }

    @Test
    fun `works at exact threshold and simple configs`() {
        val contextExact = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("20000.00"))
        val resultExact = condition.modifyBidAmount(contextExact, mapOf("20000" to 0.07), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("21400").compareTo(resultExact))
        
        val contextSimple = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("8000.00"))
        val resultSimple = condition.modifyBidAmount(contextSimple, mapOf("5000" to 0.03), BigDecimal("999.00"))
        assertEquals(0, BigDecimal("8240").compareTo(resultSimple))
    }

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, mapOf("1000" to 0.05)))
    }
}
