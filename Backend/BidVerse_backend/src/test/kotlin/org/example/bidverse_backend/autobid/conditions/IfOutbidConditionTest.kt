package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IfOutbidConditionTest {

    private val condition = IfOutbidCondition()

    @Test
    fun `should return true when condition is disabled (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when condition is disabled (false)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, false))
    }

    @Test
    fun `should return true when user is outbid`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        
        assertTrue(condition.shouldBid(context, true))
    }

    @Test
    fun `should return false when user is currently winning`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.bidder,
            currentPrice = BigDecimal("150.00")
        )
        
        assertFalse(condition.shouldBid(context, true))
    }

    @Test
    fun `should return true when user has never bid (isOutbid returns true for initial bid)`() {
        val context = ConditionTestHelpers.createSimpleContext(
            user = TestUsers.bidder,
            currentPrice = BigDecimal("100.00")
        )
        
        // isOutbid() returns true if user has never bid (should place initial bid)
        assertTrue(condition.shouldBid(context, true))
    }

    @Test
    fun `should return true when user had winning bid but got outbid by competitor`() {
        val bidHistory = listOf(
            BigDecimal("200.00"), // Competitor1 winning now
            BigDecimal("180.00"), // Bidder was winning
            BigDecimal("150.00")  // Competitor1 started
        )
        
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = bidHistory
        )
        
        assertTrue(condition.shouldBid(context, true))
    }

    @Test
    fun `should return true when no bids at all (isOutbid returns true for initial bid)`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = emptyList()
        )
        
        // isOutbid() returns true if user has never bid
        assertTrue(condition.shouldBid(context, true))
    }
}
