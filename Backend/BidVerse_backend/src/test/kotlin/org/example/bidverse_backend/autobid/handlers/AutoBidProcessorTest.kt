package org.example.bidverse_backend.autobid.handlers

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.autobid.AutoBidDecision
import org.example.bidverse_backend.autobid.conditions.ConditionHandler
import org.example.bidverse_backend.entities.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Unit tests for AutoBidProcessor - the core autobid decision engine.
 * Tests the orchestration of condition handlers and bid amount calculation.
 */
class AutoBidProcessorTest {

    private lateinit var processor: AutoBidProcessor
    private lateinit var mockConditionHandlers: List<ConditionHandler>

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
        incrementAmount: BigDecimal? = BigDecimal("10.00"),
        startingBidAmount: BigDecimal? = null,
        isActive: Boolean = true,
        conditionsJson: Map<String, Any>? = null
    ): AutoBid {
        return AutoBid(
            id = id,
            user = user,
            auction = auction,
            maxBidAmount = maxBidAmount,
            startingBidAmount = startingBidAmount,
            incrementAmount = incrementAmount,
            intervalMinutes = 5,
            nextRun = LocalDateTime.now(),
            isActive = isActive,
            conditionsJson = conditionsJson,
            lastRun = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
    }

    // Mock condition handler that always allows bidding
    private class AlwaysAllowCondition : ConditionHandler {
        override val conditionName: String = "always_allow"
        override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true
    }

    // Mock condition handler that never allows bidding
    private class NeverAllowCondition : ConditionHandler {
        override val conditionName: String = "never_allow"
        override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = false
    }

    // Mock condition handler that modifies bid amount
    private class DoubleAmountCondition : ConditionHandler {
        override val conditionName: String = "double_amount"
        override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true
        override fun modifyBidAmount(
            context: AutoBidContext,
            conditionValue: Any?,
            baseAmount: BigDecimal
        ): BigDecimal {
            return baseAmount.multiply(BigDecimal("2"))
        }
    }

    // Mock condition handler that adds fixed amount
    private class AddTenCondition : ConditionHandler {
        override val conditionName: String = "add_ten"
        override fun shouldBid(context: AutoBidContext, conditionValue: Any?): Boolean = true
        override fun modifyBidAmount(
            context: AutoBidContext,
            conditionValue: Any?,
            baseAmount: BigDecimal
        ): BigDecimal {
            return baseAmount.add(BigDecimal("10.00"))
        }
    }

    @BeforeEach
    fun setUp() {
        mockConditionHandlers = listOf(
            AlwaysAllowCondition(),
            NeverAllowCondition(),
            DoubleAmountCondition(),
            AddTenCondition()
        )
        processor = AutoBidProcessor(mockConditionHandlers)
    }

    @Test
    fun `should stop autobid when auction has ended`() {
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
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.StopAutoBid)
        assertEquals("Auction has ended", (decision as AutoBidDecision.StopAutoBid).reason)
    }

    @Test
    fun `should skip when autobid is not active`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction, isActive = false)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.SkipBid)
        assertEquals("AutoBid is not active", (decision as AutoBidDecision.SkipBid).reason)
    }

    @Test
    fun `should skip when user is already winning`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction)
        
        val winningBid = createTestBid(1, auction, user, BigDecimal("200.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = winningBid,
            allBids = listOf(winningBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.SkipBid)
        assertEquals("User is already the highest bidder", (decision as AutoBidDecision.SkipBid).reason)
    }

    @Test
    fun `should skip when no conditions configured`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val autoBid = createTestAutoBid(1, user, auction, conditionsJson = null)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.SkipBid)
        assertEquals("No conditions configured", (decision as AutoBidDecision.SkipBid).reason)
    }

    @Test
    fun `should skip when condition is not met`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner)
        val conditions = mapOf("never_allow" to true)
        val autoBid = createTestAutoBid(1, user, auction, conditionsJson = conditions)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.SkipBid)
        assertTrue((decision as AutoBidDecision.SkipBid).reason.contains("never_allow"))
    }

    @Test
    fun `should place bid with base increment when all conditions met`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf("always_allow" to true)
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("10.00"),
            conditionsJson = conditions
        )
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.PlaceBid)
        val placeBid = decision as AutoBidDecision.PlaceBid
        assertEquals(BigDecimal("110.00"), placeBid.amount)
    }

    @Test
    fun `should use starting bid amount for first bid when configured`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf("always_allow" to true)
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("10.00"),
            startingBidAmount = BigDecimal("150.00"),
            conditionsJson = conditions
        )
        
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
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.PlaceBid)
        val placeBid = decision as AutoBidDecision.PlaceBid
        assertEquals(BigDecimal("150.00"), placeBid.amount)
    }

    @Test
    fun `should not use starting bid if user has already bid`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf("always_allow" to true)
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("10.00"),
            startingBidAmount = BigDecimal("150.00"),
            conditionsJson = conditions
        )
        
        val previousBid = createTestBid(1, auction, user, BigDecimal("120.00"))
        val otherBid = createTestBid(2, auction, createTestUser(3), BigDecimal("200.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = otherBid,
            allBids = listOf(previousBid, otherBid),
            currentTime = LocalDateTime.now(),
            lastBidByThisAutoBid = previousBid
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.PlaceBid)
        val placeBid = decision as AutoBidDecision.PlaceBid
        // Should be currentPrice (200) + increment (10) = 210, not startingBid (150)
        assertEquals(BigDecimal("210.00"), placeBid.amount)
    }

    @Test
    fun `should apply condition modifiers to bid amount`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf("double_amount" to true)
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("10.00"),
            conditionsJson = conditions
        )
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.PlaceBid)
        val placeBid = decision as AutoBidDecision.PlaceBid
        // Base: 100 + 10 = 110, doubled = 220
        assertEquals(BigDecimal("220.00"), placeBid.amount)
    }

    @Test
    fun `should apply multiple condition modifiers in sequence`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf(
            "double_amount" to true,
            "add_ten" to true
        )
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("10.00"),
            conditionsJson = conditions
        )
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.PlaceBid)
        val placeBid = decision as AutoBidDecision.PlaceBid
        // Base: 100 + 10 = 110
        // After double_amount: 220
        // After add_ten: 230
        assertEquals(BigDecimal("230.00"), placeBid.amount)
    }

    @Test
    fun `should cap bid at maxBidAmount when exceeded`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf("always_allow" to true)
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("10.00"),
            maxBidAmount = BigDecimal("105.00"),
            conditionsJson = conditions
        )
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = null,
            allBids = emptyList(),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.PlaceBid)
        val placeBid = decision as AutoBidDecision.PlaceBid
        // Would be 110, but capped at 105
        assertEquals(BigDecimal("105.00"), placeBid.amount)
        assertTrue(placeBid.reason.contains("maximum"))
    }

    @Test
    fun `should skip when calculated bid is not higher than current price`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val otherUser = createTestUser(3)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf("always_allow" to true)
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("10.00"),
            maxBidAmount = BigDecimal("105.00"),
            conditionsJson = conditions
        )
        
        val highBid = createTestBid(1, auction, otherUser, BigDecimal("200.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = highBid,
            allBids = listOf(highBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.SkipBid)
        // Calculated bid would be 210, capped at 105, which is < current price 200
        assertTrue((decision as AutoBidDecision.SkipBid).reason.contains("not higher than current price"))
    }

    @Test
    fun `should calculate bid from current highest bid value`() {
        // Given
        val user = createTestUser(1)
        val owner = createTestUser(2)
        val otherUser = createTestUser(3)
        val auction = createTestAuction(100, owner, minimumPrice = BigDecimal("100.00"))
        val conditions = mapOf("always_allow" to true)
        val autoBid = createTestAutoBid(
            1, user, auction,
            incrementAmount = BigDecimal("25.00"),
            conditionsJson = conditions
        )
        
        val currentBid = createTestBid(1, auction, otherUser, BigDecimal("175.00"), isWinning = true)
        
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = currentBid,
            allBids = listOf(currentBid),
            currentTime = LocalDateTime.now()
        )
        
        // When
        val decision = processor.processAutoBid(context)
        
        // Then
        assertTrue(decision is AutoBidDecision.PlaceBid)
        val placeBid = decision as AutoBidDecision.PlaceBid
        // Should be 175 + 25 = 200
        assertEquals(BigDecimal("200.00"), placeBid.amount)
    }
}
