package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class CounterBidFactorConditionTest {

    private val condition = CounterBidFactorCondition()

    @Test
    fun `shouldBid is always true`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        assertTrue(condition.shouldBid(context, 1.5))
    }

    @Test
    fun `null or invalid config returns null`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("160.00")))
        assertNull(condition.modifyBidAmount(context, "not a number", BigDecimal("160.00")))
    }

    @Test
    fun `requires at least 2 bids`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00")),
            bidders = listOf(TestUsers.competitor1)
        )
        assertNull(condition.modifyBidAmount(context, 1.5, BigDecimal("160.00")))
    }

    @Test
    fun `calculates counter bid with factor`() {
        val context1 = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("150.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result1 = condition.modifyBidAmount(context1, 1.2, BigDecimal("160.00"))
        assertEquals(0, BigDecimal("210").compareTo(result1))
        
        val context2 = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("300.00"), BigDecimal("200.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result2 = condition.modifyBidAmount(context2, 1.5, BigDecimal("310.00"))
        assertEquals(0, BigDecimal("450").compareTo(result2))
    }

    @Test
    fun `rounds to nearest integer`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("133.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result = condition.modifyBidAmount(context, 1.5, BigDecimal("143.00"))
        assertEquals(0, BigDecimal("183").compareTo(result))
    }

    @Test
    fun `works with factor less than 1`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            bidValues = listOf(BigDecimal("200.00"), BigDecimal("100.00")),
            bidders = listOf(TestUsers.competitor1, TestUsers.competitor2)
        )
        val result = condition.modifyBidAmount(context, 0.5, BigDecimal("210.00"))
        assertEquals(0, BigDecimal("250").compareTo(result))
    }
}
