package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.entities.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime

class IfNoActivityForDdHhMmConditionTest {

    private val condition = IfNoActivityForDdHhMmCondition()

    private fun createTestContext(lastBidMinutesAgo: Long?): AutoBidContext {
        val category = Category(id = 1, categoryName = "Test")
        val owner = User(id = 1, userName = "Owner", emailAddress = "owner@test.com", phoneNumber = "1234567890", role = "USER")
        val user = User(id = 2, userName = "Bidder", emailAddress = "bidder@test.com", phoneNumber = "0987654321", role = "USER")
        val currentTime = LocalDateTime.now()
        
        val auction = Auction(
            id = 100, owner = owner, category = category, itemName = "Test Item",
            minimumPrice = BigDecimal("100.00"), createDate = currentTime.minusDays(1),
            expiredDate = currentTime.plusHours(2), lastBid = null, description = "Test",
            type = "STANDARD", extraTime = null, itemState = "NEW", tags = null,
            minStep = 10, condition = 1, startDate = currentTime.minusHours(1)
        )
        
        val autoBid = AutoBid(
            id = 1, user = user, auction = auction, maxBidAmount = BigDecimal("500.00"),
            startingBidAmount = null, incrementAmount = BigDecimal("10.00"), intervalMinutes = 5,
            nextRun = currentTime, isActive = true, conditionsJson = null, lastRun = null,
            createdAt = currentTime, updatedAt = null
        )
        
        val lastBid = lastBidMinutesAgo?.let {
            Bid(
                id = 1, auction = auction, bidder = owner,
                value = BigDecimal("150.00"),
                timeStamp = currentTime.minusMinutes(it),
                isWinning = true
            )
        }
        
        return AutoBidContext(
            autoBid = autoBid, auction = auction, user = user,
            currentHighestBid = lastBid,
            allBids = if (lastBid != null) listOf(lastBid) else emptyList(),
            currentTime = currentTime
        )
    }

    @Test
    fun `null config allows bidding`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `invalid config blocks bidding`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertFalse(condition.shouldBid(context, 123))
        assertFalse(condition.shouldBid(context, "invalid"))
        assertFalse(condition.shouldBid(context, "1_2"))
        assertFalse(condition.shouldBid(context, "a_b_c"))
    }

    @Test
    fun `bids when no activity or threshold exceeded`() {
        val contextNoBids = createTestContext(lastBidMinutesAgo = null)
        assertTrue(condition.shouldBid(contextNoBids, "0_1_0"))
        
        val context120min = createTestContext(lastBidMinutesAgo = 120)
        assertTrue(condition.shouldBid(context120min, "0_1_30"))
        
        val context60min = createTestContext(lastBidMinutesAgo = 60)
        assertTrue(condition.shouldBid(context60min, "0_1_0"))
    }

    @Test
    fun `blocks bidding when threshold not met`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertFalse(condition.shouldBid(context, "0_1_0"))
    }

    @Test
    fun `handles multi-day durations`() {
        val context3days = createTestContext(lastBidMinutesAgo = 4320)
        assertTrue(condition.shouldBid(context3days, "2_0_0"))
        
        val contextComplex = createTestContext(lastBidMinutesAgo = 3360)
        assertTrue(condition.shouldBid(contextComplex, "2_3_30"))
    }
}
