package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class ReactDelayMinutesConditionTest {

    private val condition = ReactDelayMinutesCondition()

    @Test
    fun `should return true when no delay configured`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, 0))
    }

    @Test
    fun `should return true when no competing bids yet`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = emptyList()
        )
        
        assertTrue(condition.shouldBid(context, 5))
    }

    @Test
    fun `should return false when not enough time passed`() {
        // NOTE: ReactDelayMinutesCondition uses LocalDateTime.now() instead of context.currentTime
        // So this test is timing-dependent and may be flaky
        // We create bids very recently and check immediately
        val recentTime = LocalDateTime.now().minusMinutes(2)
        
        val bidders = listOf(TestUsers.competitor1)
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = listOf(BigDecimal("150.00")),
            bidders = bidders
        )
        
        // Delay is 5 minutes, but the bid was created ~2 minutes ago (in createContextWithBidHistory)
        // Since the condition uses LocalDateTime.now(), this should fail
        assertFalse(condition.shouldBid(context, 5))
    }

    @Test
    fun `should return true when delay satisfied`() {
        // Create a context where the last bid was long ago
        val longAgo = LocalDateTime.now().minusMinutes(20)
        val now = LocalDateTime.now()
        
        val auction = ConditionTestHelpers.createAuction(
            expiredDate = now.plusHours(2)
        )
        val autoBid = ConditionTestHelpers.createAutoBid(
            user = TestUsers.bidder,
            auction = auction
        )
        val oldBid = ConditionTestHelpers.createBid(
            auction = auction,
            bidder = TestUsers.competitor1,
            value = BigDecimal("150.00"),
            timeStamp = longAgo
        )
        
        val context = org.example.bidverse_backend.autobid.AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = TestUsers.bidder,
            currentHighestBid = oldBid,
            allBids = listOf(oldBid),
            currentTime = now
        )
        
        // Delay is 5 minutes, 20 minutes have passed
        assertTrue(condition.shouldBid(context, 5))
    }

    @Test
    fun `should ignore bids from autobid user`() {
        val longAgo = LocalDateTime.now().minusMinutes(20)
        val now = LocalDateTime.now()
        
        val auction = ConditionTestHelpers.createAuction(
            expiredDate = now.plusHours(2)
        )
        val autoBid = ConditionTestHelpers.createAutoBid(
            user = TestUsers.bidder,
            auction = auction
        )
        
        val userBid = ConditionTestHelpers.createBid(
            id = 1,
            auction = auction,
            bidder = TestUsers.bidder,
            value = BigDecimal("150.00"),
            timeStamp = now.minusMinutes(1) // Recent
        )
        val competitorBid = ConditionTestHelpers.createBid(
            id = 2,
            auction = auction,
            bidder = TestUsers.competitor1,
            value = BigDecimal("140.00"),
            timeStamp = longAgo // Old
        )
        
        val context = org.example.bidverse_backend.autobid.AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = TestUsers.bidder,
            currentHighestBid = userBid,
            allBids = listOf(userBid, competitorBid),
            currentTime = now
        )
        
        // Should check last bid by OTHERS (competitorBid from 20 min ago), not user's own bid
        // getLastBidByOthers() should return the old competitor bid
        assertTrue(condition.shouldBid(context, 5))
    }
}
