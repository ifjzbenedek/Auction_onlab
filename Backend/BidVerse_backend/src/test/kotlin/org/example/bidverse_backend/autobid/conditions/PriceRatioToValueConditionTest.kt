package org.example.bidverse_backend.autobid.conditions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PriceRatioToValueConditionTest {

    private val condition = PriceRatioToValueCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when price is below ratio limit`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("120.00")
        )
        
        // minimumPrice defaults to 100, ratio 1.5 → max allowed 150
        assertTrue(condition.shouldBid(context, 1.5))
    }

    @Test
    fun `should return true when price equals ratio limit`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("150.00")
        )
        
        // minimumPrice 100, ratio 1.5 → max allowed 150
        assertTrue(condition.shouldBid(context, 1.5))
    }

    @Test
    fun `should return false when price exceeds ratio limit`() {
        // Create auction with explicit minimumPrice
        val now = java.time.LocalDateTime.now()
        val auction = ConditionTestHelpers.createAuction(
            minimumPrice = java.math.BigDecimal("100.00"),
            expiredDate = now.plusHours(2)
        )
        val autoBid = ConditionTestHelpers.createAutoBid(
            user = ConditionTestHelpers.TestUsers.bidder,
            auction = auction
        )
        val highestBid = ConditionTestHelpers.createBid(
            auction = auction,
            bidder = ConditionTestHelpers.TestUsers.competitor1,
            value = java.math.BigDecimal("200.00"),
            timeStamp = now.minusMinutes(5)
        )
        val context = org.example.bidverse_backend.autobid.AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = ConditionTestHelpers.TestUsers.bidder,
            currentHighestBid = highestBid,
            allBids = listOf(highestBid),
            currentTime = now
        )
        
        // minimumPrice 100, ratio 1.5 → max allowed 150, currentPrice 200
        assertFalse(condition.shouldBid(context, 1.5))
    }

    @Test
    fun `should handle ratio of 2`() {
        val now = java.time.LocalDateTime.now()
        val auction = ConditionTestHelpers.createAuction(
            minimumPrice = java.math.BigDecimal("100.00"),
            expiredDate = now.plusHours(2)
        )
        val autoBid = ConditionTestHelpers.createAutoBid(
            user = ConditionTestHelpers.TestUsers.bidder,
            auction = auction
        )
        val highestBid = ConditionTestHelpers.createBid(
            auction = auction,
            bidder = ConditionTestHelpers.TestUsers.competitor1,
            value = java.math.BigDecimal("250.00"),
            timeStamp = now.minusMinutes(5)
        )
        val context = org.example.bidverse_backend.autobid.AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = ConditionTestHelpers.TestUsers.bidder,
            currentHighestBid = highestBid,
            allBids = listOf(highestBid),
            currentTime = now
        )
        
        // minimumPrice 100, ratio 2.0 → max allowed 200, currentPrice 250
        assertFalse(condition.shouldBid(context, 2.0))
    }

    @Test
    fun `should handle small ratio`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("105.00")
        )
        
        // minimumPrice 100, ratio 1.2 → max allowed 120
        assertTrue(condition.shouldBid(context, 1.2))
    }

    @Test
    fun `should handle ratio of 1`() {
        val context = ConditionTestHelpers.createSimpleContext(
            currentPrice = BigDecimal("100.00")
        )
        
        // minimumPrice 100, ratio 1.0 → max allowed 100
        assertTrue(condition.shouldBid(context, 1.0))
    }
}
