package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class RandomizeIncrementConditionTest {

    private val condition = RandomizeIncrementCondition()

    @Test
    fun `should not modify when condition is disabled (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("100.00")))
    }

    @Test
    fun `should not modify when condition is disabled (false)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertNull(condition.modifyBidAmount(context, false, BigDecimal("100.00")))
    }

    @Test
    fun `should randomize increment when enabled`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        val baseAmount = BigDecimal("1100.00")
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        assertNotNull(result)
        // Increment is 100, noise is -10% to +10% → result should be 1090 to 1110
        assertTrue(result!! >= BigDecimal("1090"))
        assertTrue(result <= BigDecimal("1110"))
    }

    @Test
    fun `should produce different results on multiple calls (randomness check)`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("1000.00")
        )
        
        val baseAmount = BigDecimal("1100.00")
        val results = mutableSetOf<BigDecimal>()
        
        // Run 20 times, should get at least a few different values
        repeat(20) {
            val result = condition.modifyBidAmount(context, true, baseAmount)
            if (result != null) {
                results.add(result)
            }
        }
        
        // With random -10% to +10%, we should get some variation
        // (not guaranteed but extremely likely)
        assertTrue(results.size > 1, "Expected multiple different randomized values, got: $results")
    }

    @Test
    fun `shouldBid always returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, false))
        assertTrue(condition.shouldBid(context, true))
    }
}
