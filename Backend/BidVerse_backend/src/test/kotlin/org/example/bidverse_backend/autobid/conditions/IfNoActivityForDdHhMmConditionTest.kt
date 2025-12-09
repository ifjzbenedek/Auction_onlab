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
    fun `shouldBid should return true when conditionValue is null`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `shouldBid should return false when conditionValue is not a string`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertFalse(condition.shouldBid(context, 123))
    }

    @Test
    fun `shouldBid should return true when no bids exist`() {
        val context = createTestContext(lastBidMinutesAgo = null)
        assertTrue(condition.shouldBid(context, "0_1_0"))
    }

    @Test
    fun `shouldBid should return true when duration threshold exceeded`() {
        // Last bid was 2 hours ago (120 minutes)
        // Threshold: 0 days, 1 hour, 30 minutes = 90 minutes
        val context = createTestContext(lastBidMinutesAgo = 120)
        assertTrue(condition.shouldBid(context, "0_1_30"))
    }

    @Test
    fun `shouldBid should return false when duration threshold not exceeded`() {
        // Last bid was 30 minutes ago
        // Threshold: 0 days, 1 hour, 0 minutes = 60 minutes
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertFalse(condition.shouldBid(context, "0_1_0"))
    }

    @Test
    fun `shouldBid should handle days correctly`() {
        // Last bid was 3 days ago (4320 minutes)
        // Threshold: 2 days, 0 hours, 0 minutes = 2880 minutes
        val context = createTestContext(lastBidMinutesAgo = 4320)
        assertTrue(condition.shouldBid(context, "2_0_0"))
    }

    @Test
    fun `shouldBid should handle complex duration`() {
        // Last bid was 2 days, 4 hours, 0 minutes ago = 3360 minutes
        // Threshold: 2 days, 3 hours, 30 minutes = 3330 minutes
        val context = createTestContext(lastBidMinutesAgo = 3360)
        assertTrue(condition.shouldBid(context, "2_3_30"))
    }

    @Test
    fun `shouldBid should return false for invalid format`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertFalse(condition.shouldBid(context, "invalid"))
    }

    @Test
    fun `shouldBid should return false for incomplete format`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertFalse(condition.shouldBid(context, "1_2"))
    }

    @Test
    fun `shouldBid should return false for non-numeric values`() {
        val context = createTestContext(lastBidMinutesAgo = 30)
        assertFalse(condition.shouldBid(context, "a_b_c"))
    }

    @Test
    fun `shouldBid should handle exactly at threshold boundary`() {
        // Last bid was exactly 60 minutes ago
        // Threshold: 0 days, 1 hour, 0 minutes = 60 minutes
        val context = createTestContext(lastBidMinutesAgo = 60)
        assertTrue(condition.shouldBid(context, "0_1_0"))
    }
}
