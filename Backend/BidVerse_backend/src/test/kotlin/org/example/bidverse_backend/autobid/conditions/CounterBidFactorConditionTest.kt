package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class CounterBidFactorConditionTest {

    private val condition = CounterBidFactorCondition()

    @Test
    fun `shouldBid should always return true`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        assertTrue(condition.shouldBid(context, 1.5))
    }

    @Test
    fun `modifyBidAmount should return null when conditionValue is null`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("160.00")))
    }

    @Test
    fun `modifyBidAmount should return null when conditionValue is not a number`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        assertNull(condition.modifyBidAmount(context, "not a number", BigDecimal("160.00")))
    }

    @Test
    fun `modifyBidAmount should return null when less than 2 bids`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00")),
            bidders = listOf(TestUsers.competitor1)
        )
        assertNull(condition.modifyBidAmount(context, 1.5, BigDecimal("160.00")))
    }

    @Test
    fun `modifyBidAmount should calculate counter bid with factor 1_2`() {
        // Opponent raised from 100 to 150 (increment = 50)
        // Counter with factor 1.2: 50 * 1.2 = 60
        // New amount: 150 + 60 = 210
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result = condition.modifyBidAmount(context, 1.2, BigDecimal("160.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("210").compareTo(result))
    }

    @Test
    fun `modifyBidAmount should calculate counter bid with factor 1_5`() {
        // Opponent raised from 200 to 300 (increment = 100)
        // Counter with factor 1.5: 100 * 1.5 = 150
        // New amount: 300 + 150 = 450
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("300.00"), BigDecimal("200.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result = condition.modifyBidAmount(context, 1.5, BigDecimal("310.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("450").compareTo(result))
    }

    @Test
    fun `modifyBidAmount should round to nearest integer`() {
        // Opponent raised from 100 to 133 (increment = 33)
        // Counter with factor 1.5: 33 * 1.5 = 49.5 -> rounds to 50
        // New amount: 133 + 50 = 183
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("133.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result = condition.modifyBidAmount(context, 1.5, BigDecimal("143.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("183").compareTo(result))
    }

    @Test
    fun `modifyBidAmount should work with factor less than 1`() {
        // Opponent raised from 100 to 200 (increment = 100)
        // Counter with factor 0.5: 100 * 0.5 = 50
        // New amount: 200 + 50 = 250
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("200.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result = condition.modifyBidAmount(context, 0.5, BigDecimal("210.00"))
        
        assertNotNull(result)
        assertEquals(0, BigDecimal("250").compareTo(result))
    }
}
