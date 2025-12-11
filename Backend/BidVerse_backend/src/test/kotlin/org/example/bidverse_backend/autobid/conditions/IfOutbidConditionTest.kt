package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class IfOutbidConditionTest {

    private val condition = IfOutbidCondition()

    @Test
    fun `disabled condition returns true`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, false))
    }

    @Test
    fun `bids when user is outbid`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        
        assertTrue(condition.shouldBid(context, true))
    }

    @Test
    fun `does not bid when user is winning`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.bidder,
            currentPrice = BigDecimal("150.00")
        )
        
        assertFalse(condition.shouldBid(context, true))
    }

    @Test
    fun `bids when user has never bid`() {
        val context = ConditionTestHelpers.createSimpleContext(
            user = TestUsers.bidder,
            currentPrice = BigDecimal("100.00")
        )
        
        assertTrue(condition.shouldBid(context, true))
    }

    @Test
    fun `bids after being outbid`() {
        val bidHistory = listOf(
            BigDecimal("200.00"),
            BigDecimal("180.00"),
            BigDecimal("150.00")
        )
        
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = bidHistory
        )
        
        assertTrue(condition.shouldBid(context, true))
    }

    @Test
    fun `handles empty bid history`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = emptyList()
        )
        
        assertTrue(condition.shouldBid(context, true))
    }
}
