package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.entities.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Unit tests for LastMinuteRushCondition.
 * Tests time-based bid modification logic.
 */
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
        
        // IMPORTANT: Create a real highest bid so getCurrentPrice() returns the currentPrice parameter
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
    fun `shouldBid should always return true`() {
        // Given
        val context = createTestContext(minutesUntilEnd = 5)
        
        // When
        val result = condition.shouldBid(context, true)
        
        // Then
        assertTrue(result)
    }

    @Test
    fun `modifyBidAmount should increase by 25 percent when less than 1 minute left`() {
        // Given
        val context = createTestContext(
            minutesUntilEnd = 0, // Less than 1 minute
            currentPrice = BigDecimal("100.00"),
            increment = BigDecimal("10.00")
        )
        val baseAmount = BigDecimal("110.00") // currentPrice + increment
        
        // When
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        // Then
        assertNotNull(result)
        // New logic: currentPrice * 1.25 = 100 * 1.25 = 125
        assertEquals(0, BigDecimal("125.00").compareTo(result))
    }

    @Test
    fun `modifyBidAmount should increase by 25 percent exactly at 1 minute boundary`() {
        // Given
        val context = createTestContext(
            minutesUntilEnd = 1,
            currentPrice = BigDecimal("200.00"),
            increment = BigDecimal("20.00")
        )
        val baseAmount = BigDecimal("220.00")
        
        // When
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        // Then
        assertNotNull(result)
        // New logic: currentPrice * 1.25 = 200 * 1.25 = 250
        assertEquals(0, BigDecimal("250.00").compareTo(result))
    }

    @Test
    fun `modifyBidAmount should return null when more than 1 minute left`() {
        // Given
        val context = createTestContext(
            minutesUntilEnd = 5,
            currentPrice = BigDecimal("100.00"),
            increment = BigDecimal("10.00")
        )
        val baseAmount = BigDecimal("110.00")
        
        // When
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `modifyBidAmount should return null when condition is false`() {
        // Given
        val context = createTestContext(
            minutesUntilEnd = 0,
            currentPrice = BigDecimal("100.00"),
            increment = BigDecimal("10.00")
        )
        val baseAmount = BigDecimal("110.00")
        
        // When
        val result = condition.modifyBidAmount(context, false, baseAmount)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `modifyBidAmount should return null when conditionValue is null`() {
        // Given
        val context = createTestContext(
            minutesUntilEnd = 0,
            currentPrice = BigDecimal("100.00"),
            increment = BigDecimal("10.00")
        )
        val baseAmount = BigDecimal("110.00")
        
        // When
        val result = condition.modifyBidAmount(context, null, baseAmount)
        
        // Then
        assertNull(result)
    }

    @Test
    fun `modifyBidAmount should work with larger bid increments`() {
        // Given
        val context = createTestContext(
            minutesUntilEnd = 0,
            currentPrice = BigDecimal("500.00"),
            increment = BigDecimal("50.00")
        )
        val baseAmount = BigDecimal("550.00")
        
        // When
        val result = condition.modifyBidAmount(context, true, baseAmount)
        
        // Then
        assertNotNull(result)
        // New logic: currentPrice * 1.25 = 500 * 1.25 = 625
        assertEquals(0, BigDecimal("625.00").compareTo(result))
    }
}
