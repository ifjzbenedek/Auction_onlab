package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PriceRatioToValueConditionTest {

    private val condition = PriceRatioToValueCondition()

    @Test
    fun `null config allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `bids when price within or at ratio limit`() {
        val context120 = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("120.00"))
        assertTrue(condition.shouldBid(context120, 1.5))  // 120 < 150
        
        val context150 = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("150.00"))
        assertTrue(condition.shouldBid(context150, 1.5))  // 150 == 150 → allowed
    }

    @Test
    fun `blocks bidding when price exceeds ratio`() {
        val now = java.time.LocalDateTime.now()
        val auction = ConditionTestHelpers.createAuction(
            minimumPrice = java.math.BigDecimal("100.00"),
            expiredDate = now.plusHours(2)
        )
        val autoBid = ConditionTestHelpers.createAutoBid(
            user = ConditionTestHelpers.TestUsers.bidder,
            auction = auction
        )
        
        // Price at 200: ratio 1.5 blocks (200 > 150), ratio 2.0 allows (200 <= 200)
        val context200 = org.example.bidverse_backend.autobid.AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = ConditionTestHelpers.TestUsers.bidder,
            currentHighestBid = ConditionTestHelpers.createBid(
                auction = auction,
                bidder = ConditionTestHelpers.TestUsers.competitor1,
                value = java.math.BigDecimal("200.00"),
                timeStamp = now.minusMinutes(5)
            ),
            allBids = emptyList(),
            currentTime = now
        )
        assertFalse(condition.shouldBid(context200, 1.5))  // 200 > 150 → blocks
        assertTrue(condition.shouldBid(context200, 2.0))   // 200 <= 200 → allows
        
        // Price at 250: truly exceeds ratio 2.0
        val context250 = org.example.bidverse_backend.autobid.AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = ConditionTestHelpers.TestUsers.bidder,
            currentHighestBid = ConditionTestHelpers.createBid(
                auction = auction,
                bidder = ConditionTestHelpers.TestUsers.competitor1,
                value = java.math.BigDecimal("250.00"),
                timeStamp = now.minusMinutes(5)
            ),
            allBids = emptyList(),
            currentTime = now
        )
        assertFalse(condition.shouldBid(context250, 2.0))  // 250 > 200 → blocks
    }

    @Test
    fun `handles various ratios`() {
        val contextSmall = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("105.00"))
        assertTrue(condition.shouldBid(contextSmall, 1.2))
        
        val contextExact = ConditionTestHelpers.createSimpleContext(currentPrice = BigDecimal("100.00"))
        assertTrue(condition.shouldBid(contextExact, 1.0))
    }
}
