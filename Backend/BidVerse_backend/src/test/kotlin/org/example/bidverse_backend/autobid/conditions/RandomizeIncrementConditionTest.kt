package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class RandomizeIncrementConditionTest {

    private val condition = RandomizeIncrementCondition()

    @Test
    fun `disabled condition does nothing`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
        assertNull(condition.modifyBidAmount(context, false, BigDecimal("100.00")))
    }

    @Test
    fun `adds random noise to increment`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        val result = condition.modifyBidAmount(context, true, BigDecimal("1100.00"))
        
        assertNotNull(result)
        assertTrue(result!! >= BigDecimal("1090"))
        assertTrue(result <= BigDecimal("1110"))
    }

    @Test
    fun `produces varied results`() {
        val context = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("1000.00"))
        val results = mutableSetOf<BigDecimal>()
        
        repeat(20) {
            val result = condition.modifyBidAmount(context, true, BigDecimal("1100.00"))
            if (result != null) results.add(result)
        }
        
        assertTrue(results.size > 1)
    }

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, true))
    }
}
