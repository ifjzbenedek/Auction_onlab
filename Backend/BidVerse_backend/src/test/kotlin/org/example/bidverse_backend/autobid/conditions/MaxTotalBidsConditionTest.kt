package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MaxTotalBidsConditionTest {

    private val condition = MaxTotalBidsCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when no bids placed yet`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = emptyList()
        )
        
        assertTrue(condition.shouldBid(context, 5))
    }

    @Test
    fun `should return true when current bids less than max`() {
        val bidHistory = listOf(
            BigDecimal("120.00"), // Bidder
            BigDecimal("110.00"), // Competitor
            BigDecimal("100.00")  // Bidder
        )
        val bidders = listOf(
            TestUsers.bidder,
            TestUsers.competitor1,
            TestUsers.bidder
        )
        
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = bidHistory,
            bidders = bidders
        )
        
        // User has 2 bids (index 0 and 2), max is 5
        assertTrue(condition.shouldBid(context, 5))
    }

    @Test
    fun `should return false when max bids reached`() {
        val bidHistory = listOf(
            BigDecimal("150.00"), // Competitor
            BigDecimal("140.00"), // Bidder
            BigDecimal("130.00"), // Bidder
            BigDecimal("120.00"), // Bidder
            BigDecimal("110.00")  // Competitor
        )
        val bidders = listOf(
            TestUsers.competitor1,
            TestUsers.bidder,
            TestUsers.bidder,
            TestUsers.bidder,
            TestUsers.competitor1
        )
        
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = bidHistory,
            bidders = bidders
        )
        
        // User has 3 bids (index 1, 2, 3), max is 3
        assertFalse(condition.shouldBid(context, 3))
    }

    @Test
    fun `should return false when bids exceed max`() {
        val bidHistory = listOf(
            BigDecimal("160.00"), // Bidder
            BigDecimal("150.00"), // Competitor
            BigDecimal("140.00"), // Bidder
            BigDecimal("130.00"), // Bidder
            BigDecimal("120.00")  // Bidder
        )
        val bidders = listOf(
            TestUsers.bidder,
            TestUsers.competitor1,
            TestUsers.bidder,
            TestUsers.bidder,
            TestUsers.bidder
        )
        
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = bidHistory,
            bidders = bidders
        )
        
        // User has 4 bids, max is 2
        assertFalse(condition.shouldBid(context, 2))
    }

    @Test
    fun `should count only user bids not all bids`() {
        val bidHistory = listOf(
            BigDecimal("170.00"), // Competitor
            BigDecimal("160.00"), // Competitor
            BigDecimal("150.00"), // Bidder
            BigDecimal("140.00"), // Competitor
            BigDecimal("130.00"), // Bidder
            BigDecimal("120.00")  // Competitor
        )
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
            bidValues = bidHistory,
            bidders = bidders
        )
        
        // User has 2 bids (index 2, 4), max is 3 → should allow
        assertTrue(condition.shouldBid(context, 3))
    }

    @Test
    fun `should handle max of 1`() {
        val bidHistory = listOf(
            BigDecimal("110.00"), // Bidder
            BigDecimal("100.00")  // Competitor
        )
        val bidders = listOf(
            TestUsers.bidder,
            TestUsers.competitor1
        )
        
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = bidHistory,
            bidders = bidders
        )
        
        // User has 1 bid, max is 1 → no more bids
        assertFalse(condition.shouldBid(context, 1))
    }
}
