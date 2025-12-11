package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class ReactDelayMinutesConditionTest {

    private val condition = ReactDelayMinutesCondition()

    @Test
    fun `no delay configured allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, 0))
    }

    @Test
    fun `no bids yet allows bidding`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = emptyList()
        )
        assertTrue(condition.shouldBid(context, 5))
    }

    @Test
    fun `blocks bidding when delay not met`() {
        val context = ConditionTestHelpers.createContextWithBidHistory(
            user = TestUsers.bidder,
            bidValues = listOf(BigDecimal("150.00")),
            bidders = listOf(TestUsers.competitor1)
        )
        
        assertFalse(condition.shouldBid(context, 5))
    }

    @Test
    fun `allows bidding after delay passes`() {
        val longAgo = LocalDateTime.now().minusMinutes(20)
        val now = LocalDateTime.now()
        
        val auction = ConditionTestHelpers.createAuction(expiredDate = now.plusHours(2))
        val autoBid = ConditionTestHelpers.createAutoBid(user = TestUsers.bidder, auction = auction)
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
        
        assertTrue(condition.shouldBid(context, 5))
    }

    @Test
    fun `ignores own bids when checking delay`() {
        val longAgo = LocalDateTime.now().minusMinutes(20)
        val now = LocalDateTime.now()
        
        val auction = ConditionTestHelpers.createAuction(expiredDate = now.plusHours(2))
        val autoBid = ConditionTestHelpers.createAutoBid(user = TestUsers.bidder, auction = auction)
        
        val userBid = ConditionTestHelpers.createBid(
            id = 1,
            auction = auction,
            bidder = TestUsers.bidder,
            value = BigDecimal("150.00"),
            timeStamp = now.minusMinutes(1)
        )
        val competitorBid = ConditionTestHelpers.createBid(
            id = 2,
            auction = auction,
            bidder = TestUsers.competitor1,
            value = BigDecimal("140.00"),
            timeStamp = longAgo
        )
        
        val context = org.example.bidverse_backend.autobid.AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = TestUsers.bidder,
            currentHighestBid = userBid,
            allBids = listOf(userBid, competitorBid),
            currentTime = now
        )
        
        assertTrue(condition.shouldBid(context, 5))
    }
}
