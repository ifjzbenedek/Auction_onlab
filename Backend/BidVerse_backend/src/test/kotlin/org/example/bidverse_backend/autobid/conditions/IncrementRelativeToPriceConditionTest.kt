package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IncrementRelativeToPriceConditionTest {

    private val condition = IncrementRelativeToPriceCondition()

    @Test
    fun `null config should not modify bid`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `applies 5 percent increment correctly`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.05, BigDecimal("999.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("1050").compareTo(result))
    }

    @Test
    fun `applies 10 percent increment`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("5000.00"))
        val result = condition.modifyBidAmount(context, 0.10, BigDecimal("999.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("5500").compareTo(result))
    }

    @Test
    fun `small percentage test`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("20000.00")
        )
        
        val result = condition.modifyBidAmount(context, 0.03, BigDecimal("999.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("20600").compareTo(result))
    }

    @Test
    fun `rounds to nearest integer`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1234.00")
        )
        
        // 1234 * 0.055 = 67.87, so total should round to 1302
        val result = condition.modifyBidAmount(context, 0.055, BigDecimal("999.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("1302").compareTo(result))
    }

    @Test
    fun `handles large percentage`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("800.00"))
        
        val result = condition.modifyBidAmount(context, 0.25, BigDecimal("999.00"))
        assertEquals(0, BigDecimal("1000").compareTo(result))
    }

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, 0.05))
    }
}
