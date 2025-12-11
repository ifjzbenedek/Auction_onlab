package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.entities.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime

class LastMinuteRushConditionTest {

    private val condition = LastMinuteRushCondition()

    private fun createTestContext(
        minutesUntilEnd: Long,
        currentPrice: BigDecimal = BigDecimal("100.00"),
        increment: BigDecimal = BigDecimal("10.00")
    ): AutoBidContext {
        val category = Category(id = 1, categoryName = "Test")
        val owner = User(
            id = 1,
            userName = "Owner",
            emailAddress = "owner@test.com",
            phoneNumber = "1234567890",
            role = "USER"
        )
        val user = User(
            id = 2,
            userName = "Bidder",
            emailAddress = "bidder@test.com",
            phoneNumber = "0987654321",
            role = "USER"
        )
        
        val currentTime = LocalDateTime.now()
        val expiredDate = currentTime.plusMinutes(minutesUntilEnd)
        
        val auction = Auction(
            id = 100,
            owner = owner,
            category = category,
            itemName = "Test Item",
            minimumPrice = BigDecimal("100.00"),
            createDate = currentTime.minusDays(1),
            expiredDate = expiredDate,
            lastBid = currentPrice,
            description = "Test",
            type = "STANDARD",
            extraTime = null,
            itemState = "NEW",
            tags = null,
            minStep = 10,
            condition = 1,
            startDate = currentTime.minusHours(1)
        )
        
        val autoBid = AutoBid(
            id = 1,
            user = user,
            auction = auction,
            maxBidAmount = BigDecimal("500.00"),
            startingBidAmount = null,
            incrementAmount = increment,
            intervalMinutes = 5,
            nextRun = currentTime,
            isActive = true,
            conditionsJson = null,
            lastRun = null,
            createdAt = currentTime,
            updatedAt = null
        )
        
        val highestBid = if (currentPrice > auction.minimumPrice) {
            Bid(
                id = 99,
                auction = auction,
                bidder = owner,
                value = currentPrice,
                timeStamp = currentTime.minusMinutes(5),
                isWinning = true
            )
        } else null
        
        return AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = highestBid,
            allBids = if (highestBid != null) listOf(highestBid) else emptyList(),
            currentTime = currentTime
        )
    }

    @Test
    fun `shouldBid is always true`() {
        val context = createTestContext(minutesUntilEnd = 5)
        assertTrue(condition.shouldBid(context, true))
    }

    @Test
    fun `increases bid by 25 percent in last minute`() {
        val context0 = createTestContext(
            minutesUntilEnd = 0,
            currentPrice = BigDecimal("100.00")
        )
        val result0 = condition.modifyBidAmount(context0, true, BigDecimal("110.00"))
        assertEquals(0, BigDecimal("125.00").compareTo(result0))
        
        val context1 = createTestContext(
            minutesUntilEnd = 1,
            currentPrice = BigDecimal("200.00")
        )
        val result1 = condition.modifyBidAmount(context1, true, BigDecimal("220.00"))
        assertEquals(0, BigDecimal("250.00").compareTo(result1))
    }

    @Test
    fun `no change when more than 1 minute left`() {
        val context = createTestContext(minutesUntilEnd = 5, currentPrice = BigDecimal("100.00"))
        assertNull(condition.modifyBidAmount(context, true, BigDecimal("110.00")))
    }

    @Test
    fun `disabled condition does nothing`() {
        val context = createTestContext(minutesUntilEnd = 0, currentPrice = BigDecimal("100.00"))
        assertNull(condition.modifyBidAmount(context, false, BigDecimal("110.00")))
        assertNull(condition.modifyBidAmount(context, null, BigDecimal("110.00")))
    }

    @Test
    fun `works with larger amounts`() {
        val context = createTestContext(
            minutesUntilEnd = 0,
            currentPrice = BigDecimal("500.00")
        )
        val result = condition.modifyBidAmount(context, true, BigDecimal("550.00"))
        assertEquals(0, BigDecimal("625.00").compareTo(result))
    }
}
