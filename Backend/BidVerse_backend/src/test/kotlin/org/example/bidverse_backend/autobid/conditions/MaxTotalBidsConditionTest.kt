package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MaxTotalBidsConditionTest {

    private val condition = MaxTotalBidsCondition()

    @Test
    fun `null config allows unlimited bids`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `allows bids when under limit`() {
        val contextNoBids = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = emptyList()
        )
        assertTrue(condition.shouldBid(contextNoBids, 5))
        
        val contextTwoBids = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = listOf(BigDecimal("120"), BigDecimal("110"), BigDecimal("100")),
            bidders = listOf(TestUsers.bidder, TestUsers.competitor1, TestUsers.bidder)
        )
        assertTrue(condition.shouldBid(contextTwoBids, 5))
    }

    @Test
    fun `blocks bids when limit reached`() {
        val bidders = listOf(
            TestUsers.competitor1,
            TestUsers.bidder,
            TestUsers.bidder,
            TestUsers.bidder,
            TestUsers.competitor1
        )
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = listOf(BigDecimal("150"), BigDecimal("140"), BigDecimal("130"), BigDecimal("120"), BigDecimal("110")),
            bidders = bidders
        )
        
        assertFalse(condition.shouldBid(context, 3))
        assertFalse(condition.shouldBid(context, 2))
    }

    @Test
    fun `counts only user bids`() {
        val bidders = listOf(
            TestUsers.competitor1,
            TestUsers.competitor1,
            TestUsers.bidder,
            TestUsers.competitor1,
            TestUsers.bidder,
            TestUsers.competitor1
        )
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = listOf(BigDecimal("170"), BigDecimal("160"), BigDecimal("150"), BigDecimal("140"), BigDecimal("130"), BigDecimal("120")),
            bidders = bidders
        )
        
        assertTrue(condition.shouldBid(context, 3))
    }

    @Test
    fun `handles single bid limit`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = listOf(BigDecimal("110"), BigDecimal("100")),
            bidders = listOf(TestUsers.bidder, TestUsers.competitor1)
        )
        
        assertFalse(condition.shouldBid(context, 1))
    }
}
