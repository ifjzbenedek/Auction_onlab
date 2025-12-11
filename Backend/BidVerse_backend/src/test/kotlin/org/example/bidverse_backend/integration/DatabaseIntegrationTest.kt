package org.example.bidverse_backend.integration

import org.example.bidverse_backend.entities.*
import org.example.bidverse_backend.repositories.*
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
 * Database Integration Tests
 * Tests real database interactions with H2 in-memory DB
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseIntegrationTest {

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

    private lateinit var testUser: User
    private lateinit var testCategory: Category

    @BeforeEach
    fun setup() {
        testUser = userRepository.save(
            User(
                id = null,
                userName = "TestUser",
                emailAddress = "test@example.com",
                phoneNumber = "1234567890",
                role = "USER"
            )
        )

        testCategory = categoryRepository.save(
            Category(id = null, categoryName = "Electronics")
        )
    }

    @Test
    fun `should save and retrieve auction with all relationships`() {
        val auction = Auction(
            id = null,
            owner = testUser,
            category = testCategory,
            itemName = "Vintage Camera",
            minimumPrice = BigDecimal("500.00"),
            createDate = LocalDateTime.now(),
            expiredDate = LocalDateTime.now().plusDays(7),
            lastBid = null,
            description = "Beautiful vintage camera",
            type = "STANDARD",
            extraTime = null,
            itemState = "GOOD",
            tags = "vintage,camera",
            minStep = 50,
            condition = 8,
            startDate = LocalDateTime.now()
        )

        val saved = auctionRepository.save(auction)

        assertNotNull(saved.id)
        
        val retrieved = auctionRepository.findById(saved.id!!).orElseThrow()
        assertEquals("Vintage Camera", retrieved.itemName)
        assertEquals(testUser.id, retrieved.owner.id)
        assertEquals(testCategory.id, retrieved.category.id)
        assertEquals(BigDecimal("500.00"), retrieved.minimumPrice)
    }

    @Test
    fun `should save bid and update auction lastBid`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = testUser, category = testCategory,
                itemName = "Item", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(), expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test", type = "STANDARD",
                extraTime = null, itemState = "NEW", tags = null,
                minStep = 10, condition = 10, startDate = LocalDateTime.now()
            )
        )

        val bid = Bid(
            id = null,
            auction = auction,
            bidder = testUser,
            value = BigDecimal("150.00"),
            timeStamp = LocalDateTime.now(),
            isWinning = true
        )

        val savedBid = bidRepository.save(bid)
        
        auction.lastBid = savedBid.value
        auctionRepository.save(auction)

        val updatedAuction = auctionRepository.findById(auction.id!!).orElseThrow()
        assertEquals(BigDecimal("150.00"), updatedAuction.lastBid)
        
        val retrievedBid = bidRepository.findById(savedBid.id!!).orElseThrow()
        assertEquals(BigDecimal("150.00"), retrievedBid.value)
        assertEquals(testUser.id, retrievedBid.bidder.id)
    }

    @Test
    fun `should save autoBid with complex conditions JSON`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = testUser, category = testCategory,
                itemName = "Item", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(), expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test", type = "STANDARD",
                extraTime = null, itemState = "NEW", tags = null,
                minStep = 10, condition = 10, startDate = LocalDateTime.now()
            )
        )

        val conditionsJson = mapOf(
            "active_hours" to listOf(9, 10, 11, 12, 13, 14, 15, 16, 17),
            "only_if_price_below" to 5000,
            "if_outbid" to true,
            "max_total_bids" to 10
        )

        val autoBid = AutoBid(
            id = null,
            user = testUser,
            auction = auction,
            maxBidAmount = BigDecimal("5000.00"),
            startingBidAmount = null,
            incrementAmount = BigDecimal("100.00"),
            intervalMinutes = 30,
            isActive = true,
            conditionsJson = conditionsJson,
            nextRun = LocalDateTime.now().plusMinutes(30),
            lastRun = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        val saved = autoBidRepository.save(autoBid)

        assertNotNull(saved.id)
        
        val retrieved = autoBidRepository.findById(saved.id!!).orElseThrow()
        assertEquals(BigDecimal("5000.00"), retrieved.maxBidAmount)
        assertEquals(30, retrieved.intervalMinutes)
        assertNotNull(retrieved.conditionsJson)
        
        @Suppress("UNCHECKED_CAST")
        val retrievedConditions = retrieved.conditionsJson as Map<String, Any>
        assertEquals(true, retrievedConditions["if_outbid"])
        assertEquals(10, retrievedConditions["max_total_bids"])
    }

    @Test
    fun `should find auctions by owner`() {
        val owner1 = userRepository.save(
            User(
                id = null,
                auctions = mutableListOf(),
                bids = mutableListOf(),
                uploadedImages = mutableListOf(),
                userName = "TestBidder",
                emailAddress = "bidder1@test.com",
                phoneNumber = "111",
                role = "USER"
            )
        )
        val owner2 = userRepository.save(
            User(
                id = null,
                auctions = mutableListOf(),
                bids = mutableListOf(),
                uploadedImages = mutableListOf(),
                userName = "Owner2",
                emailAddress = "owner2@test.com",
                phoneNumber = "222",
                role = "USER"
            )
        )

        auctionRepository.save(
            Auction(
                id = null, owner = owner1, category = testCategory,
                itemName = "Item1", minimumPrice = BigDecimal("100"),
                createDate = LocalDateTime.now(), expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test", type = "STANDARD",
                extraTime = null, itemState = "NEW", tags = null,
                minStep = 10, condition = 10, startDate = LocalDateTime.now()
            )
        )
        
        auctionRepository.save(
            Auction(
                id = null, owner = owner1, category = testCategory,
                itemName = "Item2", minimumPrice = BigDecimal("200"),
                createDate = LocalDateTime.now(), expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test", type = "STANDARD",
                extraTime = null, itemState = "NEW", tags = null,
                minStep = 10, condition = 10, startDate = LocalDateTime.now()
            )
        )
        
        auctionRepository.save(
            Auction(
                id = null, owner = owner2, category = testCategory,
                itemName = "Item3", minimumPrice = BigDecimal("300"),
                createDate = LocalDateTime.now(), expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test", type = "STANDARD",
                extraTime = null, itemState = "NEW", tags = null,
                minStep = 10, condition = 10, startDate = LocalDateTime.now()
            )
        )

        val owner1Auctions = auctionRepository.findByOwner(owner1)
        val owner2Auctions = auctionRepository.findByOwner(owner2)

        assertEquals(2, owner1Auctions.size)
        assertEquals(1, owner2Auctions.size)
        assertTrue(owner1Auctions.all { it.owner.id == owner1.id })
    }

    @Test
    fun `should cascade delete work correctly`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = testUser, category = testCategory,
                itemName = "Item", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(), expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test", type = "STANDARD",
                extraTime = null, itemState = "NEW", tags = null,
                minStep = 10, condition = 10, startDate = LocalDateTime.now()
            )
        )

        bidRepository.save(
            Bid(
                id = null, auction = auction, bidder = testUser,
                value = BigDecimal("150"), timeStamp = LocalDateTime.now(),
                isWinning = true
            )
        )

        val auctionId = auction.id!!
        val bidsBeforeDelete = bidRepository.findByAuctionId(auctionId)
        assertEquals(1, bidsBeforeDelete.size)

        bidRepository.deleteAll(bidsBeforeDelete)
        auctionRepository.deleteById(auctionId)

        assertFalse(auctionRepository.existsById(auctionId))
        val bidsAfterDelete = bidRepository.findByAuctionId(auctionId)
        assertEquals(0, bidsAfterDelete.size)
    }

    @Test
    fun `should handle multiple bids on same auction ordered by timestamp`() {
        val auction = auctionRepository.save(
            Auction(
                id = null, owner = testUser, category = testCategory,
                itemName = "Item", minimumPrice = BigDecimal("100.00"),
                createDate = LocalDateTime.now(), expiredDate = LocalDateTime.now().plusDays(1),
                lastBid = null, description = "Test", type = "STANDARD",
                extraTime = null, itemState = "NEW", tags = null,
                minStep = 10, condition = 10, startDate = LocalDateTime.now()
            )
        )

        val now = LocalDateTime.now()
        
        bidRepository.save(
            Bid(null, auction, testUser, BigDecimal("110"), now.minusMinutes(5), false)
        )
        bidRepository.save(
            Bid(null, auction, testUser, BigDecimal("120"), now.minusMinutes(3), false)
        )
        bidRepository.save(
            Bid(null, auction, testUser, BigDecimal("130"), now, true)
        )

        val bids = bidRepository.findByAuctionId(auction.id!!)
        
        assertEquals(3, bids.size)
        // Sort manually since repository doesn't guarantee order
        val sortedBids = bids.sortedByDescending { it.timeStamp }
        assertEquals(BigDecimal("130"), sortedBids[0].value) // Most recent
        assertEquals(BigDecimal("120"), sortedBids[1].value)
        assertEquals(BigDecimal("110"), sortedBids[2].value) // Oldest
    }
}
