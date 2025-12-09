package org.example.bidverse_backend.integration

import org.example.bidverse_backend.entities.*
import org.example.bidverse_backend.repositories.*
import org.example.bidverse_backend.services.AuctionService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Service Layer Integration Tests
 * Tests business logic services with real database persistence
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceLayerIntegrationTest {

    @Autowired
    lateinit var auctionService: AuctionService

    @Autowired
    lateinit var auctionRepository: AuctionRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var bidRepository: BidRepository

    @Autowired
    lateinit var autoBidRepository: AutoBidRepository

    private lateinit var owner: User
    private lateinit var bidder: User
    private lateinit var category: Category

    @BeforeEach
    fun setup() {
        owner = userRepository.save(
            User(
                id = null,
                auctions = mutableListOf(),
                bids = mutableListOf(),
                uploadedImages = mutableListOf(),
                userName = "AuctionOwner",
                emailAddress = "owner@test.com",
                phoneNumber = "111",
                role = "USER"
            )
        )
        
        bidder = userRepository.save(
            User(
                id = null,
                auctions = mutableListOf(),
                bids = mutableListOf(),
                uploadedImages = mutableListOf(),
                userName = "AutoBidUser",
                emailAddress = "bidder@test.com",
                phoneNumber = "222",
                role = "USER"
            )
        )

        category = categoryRepository.save(
            Category(id = null, categoryName = "Test Category")
        )
    }

    @Test
    fun `auction service should retrieve all auctions`() {
        // Create test auctions
        auctionRepository.save(
            Auction(
                id = null, owner = owner, category = category,
                itemName = "Item 1", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(), 
                expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test",
                type = "STANDARD", extraTime = null, itemState = "NEW",
                tags = null, minStep = 10, condition = 10,
                startDate = LocalDateTime.now()
            )
        )

        auctionRepository.save(
            Auction(
                id = null, owner = owner, category = category,
                itemName = "Item 2", minimumPrice = BigDecimal("200.00"),
                createDate = LocalDateTime.now(),
                expiredDate = LocalDateTime.now().plusDays(2),
                lastBid = null, description = "Test 2",
                type = "STANDARD", extraTime = null, itemState = "NEW",
                tags = null, minStep = 20, condition = 9,
                startDate = LocalDateTime.now()
            )
        )

        val allAuctions = auctionService.getAllAuctions(null, null, null)
        
        assertTrue(allAuctions.size >= 2)
        assertTrue(allAuctions.any { it.itemName == "Item 1" })
        assertTrue(allAuctions.any { it.itemName == "Item 2" })
    }

    @Test
    fun `auction service should find auction by id`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = owner, category = category,
                itemName = "Test Item", minimumPrice = BigDecimal("150.00"),
                createDate = LocalDateTime.now(),
                expiredDate = LocalDateTime.now().plusDays(3),
                lastBid = null, description = "Description",
                type = "STANDARD", extraTime = null, itemState = "GOOD",
                tags = "test,item", minStep = 15, condition = 8,
                startDate = LocalDateTime.now()
            )
        )

        val found = auctionRepository.findById(auction.id!!).orElse(null)
        
        assertNotNull(found)
        assertEquals("Test Item", found!!.itemName)
        assertEquals(BigDecimal("150.00"), found.minimumPrice)
    }

    @Test
    fun `auction service should return null for non-existent id`() {
        val found = auctionRepository.findById(99999).orElse(null)
        assertNull(found)
    }

    @Test
    fun `bid repository should save and persist bids correctly`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = owner, category = category,
                itemName = "Item", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(),
                expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = BigDecimal("100.00"), description = "Test",
                type = "STANDARD", extraTime = null, itemState = "NEW",
                tags = null, minStep = 10, condition = 10,
                startDate = LocalDateTime.now()
            )
        )

        val bid = Bid(
            id = null, auction = auction, bidder = bidder,
            value = BigDecimal("150.00"),
            timeStamp = LocalDateTime.now(), isWinning = true
        )

        bidRepository.save(bid)

        // Verify bid was saved
        val savedBids = bidRepository.findByAuctionId(auction.id!!)
        assertEquals(1, savedBids.size)
        assertEquals(BigDecimal("150.00"), savedBids[0].value)
    }

    @Test
    fun `autobid repository should save and retrieve complex conditions`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = owner, category = category,
                itemName = "Item", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(),
                expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test",
                type = "STANDARD", extraTime = null, itemState = "NEW",
                tags = null, minStep = 10, condition = 10,
                startDate = LocalDateTime.now()
            )
        )

        val conditionsJson = mapOf(
            "active_hours" to listOf(9, 10, 11, 12, 13, 14, 15, 16, 17),
            "only_if_price_below" to 5000,
            "if_outbid" to true,
            "max_total_bids" to 10,
            "avoid_user_ids" to listOf(1, 2, 3)
        )

        val autoBid = autoBidRepository.save(
            AutoBid(
                id = null, user = bidder, auction = auction,
                maxBidAmount = BigDecimal("5000.00"),
                startingBidAmount = BigDecimal("150.00"),
                incrementAmount = BigDecimal("100.00"),
                intervalMinutes = 30, isActive = true,
                conditionsJson = conditionsJson,
                nextRun = LocalDateTime.now().plusMinutes(30),
                lastRun = null, createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        )

        val retrieved = autoBidRepository.findById(autoBid.id!!).orElseThrow()
        
        assertNotNull(retrieved.conditionsJson)
        @Suppress("UNCHECKED_CAST")
        val retrievedConditions = retrieved.conditionsJson as Map<String, Any>
        assertEquals(true, retrievedConditions["if_outbid"])
        assertEquals(10, retrievedConditions["max_total_bids"])
        assertEquals(5000, retrievedConditions["only_if_price_below"])
    }

    @Test
    fun `repository should handle concurrent bids on same auction`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = owner, category = category,
                itemName = "Hot Item", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(),
                expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = BigDecimal("100.00"), description = "Popular",
                type = "STANDARD", extraTime = null, itemState = "NEW",
                tags = null, minStep = 10, condition = 10,
                startDate = LocalDateTime.now()
            )
        )

        val bidder2 = userRepository.save(
            User(
                id = null,
                auctions = mutableListOf(),
                bids = mutableListOf(),
                uploadedImages = mutableListOf(),
                userName = "Bidder2",
                emailAddress = "bidder2@test.com",
                phoneNumber = "444",
                role = "USER"
            )
        )

        // Simulate concurrent bids
        val bid1 = Bid(null, auction, bidder, BigDecimal("110.00"), LocalDateTime.now(), false)
        val bid2 = Bid(null, auction, bidder2, BigDecimal("120.00"), LocalDateTime.now().plusSeconds(1), true)

        bidRepository.save(bid1)
        bidRepository.save(bid2)

        val allBids = bidRepository.findByAuctionId(auction.id!!)
        assertEquals(2, allBids.size)
        
        val sortedBids = allBids.sortedByDescending { it.value }
        assertEquals(BigDecimal("120.00"), sortedBids[0].value)
        assertEquals(BigDecimal("110.00"), sortedBids[1].value)
    }
}
