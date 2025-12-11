package org.example.bidverse_backend.autobid

import org.example.bidverse_backend.entities.AutoBid
import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.entities.Bid
import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.entities.Category
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Unit tests for AutoBidContext helper methods.
 * Tests the context data and calculations used by all condition handlers.
 */
class AutoBidContextTest {

    // Helper to create test users
    private fun createTestUser(id: Int, name: String = "User$id"): User {
        return User(
            id = id,
            userName = name,
            emailAddress = "$name@test.com",
            phoneNumber = "1234567890",
            role = "USER"
        )
    }

    // Helper to create test auction
    private fun createTestAuction(
        id: Int,
        owner: User,
        minimumPrice: BigDecimal = BigDecimal("100.00"),
        expiredDate: LocalDateTime = LocalDateTime.now().plusHours(2)
    ): Auction {
        val category = Category(id = 1, categoryName = "Test")
        return Auction(
            id = id,
            owner = owner,
            category = category,
            itemName = "Test Item",
            minimumPrice = minimumPrice,
            createDate = LocalDateTime.now().minusDays(1),
            expiredDate = expiredDate,
            lastBid = null,
            description = "Test description",
            type = "STANDARD",
            extraTime = null,
            itemState = "NEW",
            tags = null,
            minStep = 10,
            condition = 1,
            startDate = null
        )
    }

    // Helper to create test bid
    private fun createTestBid(
        id: Int,
        auction: Auction,
        bidder: User,
        value: BigDecimal,
        timeStamp: LocalDateTime = LocalDateTime.now(),
        isWinning: Boolean = false
    ): Bid {
        return Bid(
            id = id,
            auction = auction,
            bidder = bidder,
            value = value,
            timeStamp = timeStamp,
            isWinning = isWinning
        )
    }

    // Helper to create test autobid
    private fun createTestAutoBid(
        id: Int,
        user: User,
        auction: Auction,
        maxBidAmount: BigDecimal? = BigDecimal("500.00"),
        incrementAmount: BigDecimal? = BigDecimal("10.00")
    ): AutoBid {
        return AutoBid(
            id = id,
            user = user,
            auction = auction,
            maxBidAmount = maxBidAmount,
            startingBidAmount = null,
            incrementAmount = incrementAmount,
            intervalMinutes = 5,
            nextRun = LocalDateTime.now(),
            isActive = true,
            conditionsJson = null,
            lastRun = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    }

    @Test
    fun `getCurrentPrice should return highest bid value when bids exist`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val autoBid = createTestAutoBid(1, user, auction)
        
        val highestBid = createTestBid(1, auction, user, BigDecimal("250.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = highestBid,
            allBids = listOf(highestBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val currentPrice = context.getCurrentPrice()
        
        // Then
        assertEquals(BigDecimal("250.00"), currentPrice)
    }

    @Test
    fun `getCurrentPrice should return minimum price when no bids exist`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val currentPrice = context.getCurrentPrice()
        
        // Then
        assertEquals(BigDecimal("100.00"), currentPrice)
    }

    @Test
    fun `isUserWinning should return true when user has highest bid`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val highestBid = createTestBid(1, auction, user, BigDecimal("250.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = highestBid,
            allBids = listOf(highestBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val isWinning = context.isUserWinning()
        
        // Then
        assertTrue(isWinning)
    }

    @Test
    fun `isUserWinning should return false when another user has highest bid`() {
        // Given
        val user = createTestUser(1)
        val otherUser = createTestUser(3)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val highestBid = createTestBid(1, auction, otherUser, BigDecimal("250.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = highestBid,
            allBids = listOf(highestBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val isWinning = context.isUserWinning()
        
        // Then
        assertFalse(isWinning)
    }

    @Test
    fun `isUserWinning should return false when no bids exist`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val isWinning = context.isUserWinning()
        
        // Then
        assertFalse(isWinning)
    }

    @Test
    fun `isOutbid should return true when user has never bid`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now(),
            lastBidByThisAutoBid = null
        )
        
        // When
        val isOutbid = context.isOutbid()
        
        // Then
        assertTrue(isOutbid)
    }

    @Test
    fun `isOutbid should return true when user bid but is no longer winning`() {
        // Given
        val user = createTestUser(1)
        val otherUser = createTestUser(3)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val userBid = createTestBid(1, auction, user, BigDecimal("200.00"), isWinning = false)
        val otherBid = createTestBid(2, auction, otherUser, BigDecimal("250.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = otherBid,
            allBids = listOf(userBid, otherBid),
            currentTime = LocalDateTime.now(),
            lastBidByThisAutoBid = userBid
        )
        
        // When
        val isOutbid = context.isOutbid()
        
        // Then
        assertTrue(isOutbid)
    }

    @Test
    fun `isOutbid should return false when user is still winning`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val userBid = createTestBid(1, auction, user, BigDecimal("250.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = userBid,
            allBids = listOf(userBid),
            currentTime = LocalDateTime.now(),
            lastBidByThisAutoBid = userBid
        )
        
        // When
        val isOutbid = context.isOutbid()
        
        // Then
        assertFalse(isOutbid)
    }

    @Test
    fun `isAuctionEnded should return true when current time is after expiredDate`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val expiredDate = LocalDateTime.now().minusHours(1)
        val auction = createTestAuction(100, owner, expiredDate = expiredDate)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val isEnded = context.isAuctionEnded()
        
        // Then
        assertTrue(isEnded)
    }

    @Test
    fun `isAuctionEnded should return false when current time is before expiredDate`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val expiredDate = LocalDateTime.now().plusHours(2)
        val auction = createTestAuction(100, owner, expiredDate = expiredDate)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val isEnded = context.isAuctionEnded()
        
        // Then
        assertFalse(isEnded)
    }

    @Test
    fun `getMinutesUntilEnd should calculate correct duration`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val currentTime = LocalDateTime.now()
        val expiredDate = currentTime.plusMinutes(45)
        val auction = createTestAuction(100, owner, expiredDate = expiredDate)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = currentTime
        )
        
        // When
        val minutesUntilEnd = context.getMinutesUntilEnd()
        
        // Then
        assertEquals(45L, minutesUntilEnd)
    }

    @Test
    fun `getSecondsUntilEnd should calculate correct duration`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val currentTime = LocalDateTime.now()
        val expiredDate = currentTime.plusSeconds(120)
        val auction = createTestAuction(100, owner, expiredDate = expiredDate)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = currentTime
        )
        
        // When
        val secondsUntilEnd = context.getSecondsUntilEnd()
        
        // Then
        assertEquals(120L, secondsUntilEnd)
    }

    @Test
    fun `getBidCountForThisAutoBid should count only user's bids`() {
        // Given
        val user = createTestUser(1)
        val otherUser = createTestUser(3)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val userBid1 = createTestBid(1, auction, user, BigDecimal("150.00"))
        val otherBid = createTestBid(2, auction, otherUser, BigDecimal("200.00"))
        val userBid2 = createTestBid(3, auction, user, BigDecimal("250.00"))
        val userBid3 = createTestBid(4, auction, user, BigDecimal("300.00"))
        
        val allBids = listOf(userBid1, otherBid, userBid2, userBid3)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = userBid3,
            allBids = allBids,
            currentTime = LocalDateTime.now()
        )
        
        // When
        val bidCount = context.getBidCountForThisAutoBid()
        
        // Then
        assertEquals(3, bidCount)
    }

    @Test
    fun `getBidCountForThisAutoBid should return 0 when user has no bids`() {
        // Given
        val user = createTestUser(1)
        val otherUser = createTestUser(3)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val otherBid = createTestBid(1, auction, otherUser, BigDecimal("200.00"))
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = otherBid,
            allBids = listOf(otherBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val bidCount = context.getBidCountForThisAutoBid()
        
        // Then
        assertEquals(0, bidCount)
    }

    @Test
    fun `getLastBidByOthers should return most recent bid from other users`() {
        // Given
        val user = createTestUser(1)
        val otherUser1 = createTestUser(3)
        val otherUser2 = createTestUser(4)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val now = LocalDateTime.now()
        val userBid = createTestBid(1, auction, user, BigDecimal("150.00"), timeStamp = now.minusMinutes(5))
        val otherBid1 = createTestBid(2, auction, otherUser1, BigDecimal("200.00"), timeStamp = now.minusMinutes(3))
        val otherBid2 = createTestBid(3, auction, otherUser2, BigDecimal("250.00"), timeStamp = now.minusMinutes(1))
        
        val allBids = listOf(userBid, otherBid1, otherBid2)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = otherBid2,
            allBids = allBids,
            currentTime = LocalDateTime.now()
        )
        
        // When
        val lastBidByOthers = context.getLastBidByOthers()
        
        // Then
        assertNotNull(lastBidByOthers)
        assertEquals(otherBid2.id, lastBidByOthers?.id)
        assertEquals(BigDecimal("250.00"), lastBidByOthers?.value)
    }

    @Test
    fun `getLastBidByOthers should return null when no other users have bid`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val userBid = createTestBid(1, auction, user, BigDecimal("150.00"))
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = userBid,
            allBids = listOf(userBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val lastBidByOthers = context.getLastBidByOthers()
        
        // Then
        assertNull(lastBidByOthers)
    }

    @Test
    fun `getLastBidByOthers should return null when no bids exist`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val lastBidByOthers = context.getLastBidByOthers()
        
        // Then
        assertNull(lastBidByOthers)
    }
}
