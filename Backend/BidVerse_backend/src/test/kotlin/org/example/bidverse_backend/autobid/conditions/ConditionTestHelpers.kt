package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.AutoBidContext
import org.example.bidverse_backend.entities.*
import java.math.BigDecimal
import java.time.LocalDateTime

// Helper utilities for condition testing
object ConditionTestHelpers {

    object TestUsers {
        val owner = createUser(1, "Owner")
        val bidder = createUser(2, "Bidder")
        val competitor1 = createUser(3, "Competitor1")
        val competitor2 = createUser(4, "Competitor2")
        val friend = createUser(5, "Friend")
        
        fun createUser(id: Int, name: String = "User$id"): User {
            return User(
                id = id,
                userName = name,
                emailAddress = "$name@test.com",
                phoneNumber = "1234567890",
                role = "USER"
            )
        }
    }

    val testCategory = Category(id = 1, categoryName = "Electronics")

    fun createAuction(
        id: Int = 100,
        owner: User = TestUsers.owner,
        category: Category = testCategory,
        itemName: String = "Test Item",
        minimumPrice: BigDecimal = BigDecimal("100.00"),
        createDate: LocalDateTime = LocalDateTime.now().minusDays(1),
        expiredDate: LocalDateTime = LocalDateTime.now().plusHours(2),
        lastBid: BigDecimal? = null,
        description: String = "Test auction description",
        type: String = "STANDARD",
        extraTime: Int? = null,
        itemState: String = "NEW",
        tags: String? = null,
        minStep: Int = 10,
        condition: Int = 1,
        startDate: LocalDateTime? = LocalDateTime.now().minusHours(1)
    ): Auction {
        return Auction(
            id = id,
            owner = owner,
            category = category,
            itemName = itemName,
            minimumPrice = minimumPrice,
            createDate = createDate,
            expiredDate = expiredDate,
            lastBid = lastBid,
            description = description,
            type = type,
            extraTime = extraTime,
            itemState = itemState,
            tags = tags,
            minStep = minStep,
            condition = condition,
            startDate = startDate
        )
    }

    fun createBid(
        id: Int = 1,
        auction: Auction,
        bidder: User = TestUsers.competitor1,
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

    fun createAutoBid(
        id: Int = 1,
        user: User = TestUsers.bidder,
        auction: Auction,
        maxBidAmount: BigDecimal? = BigDecimal("500.00"),
        startingBidAmount: BigDecimal? = null,
        incrementAmount: BigDecimal? = BigDecimal("10.00"),
        intervalMinutes: Int? = 5,
        nextRun: LocalDateTime? = LocalDateTime.now(),
        isActive: Boolean = true,
        conditionsJson: Map<String, Any>? = null,
        lastRun: LocalDateTime? = null,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime? = null
    ): AutoBid {
        return AutoBid(
            id = id,
            user = user,
            auction = auction,
            maxBidAmount = maxBidAmount,
            startingBidAmount = startingBidAmount,
            incrementAmount = incrementAmount,
            intervalMinutes = intervalMinutes,
            nextRun = nextRun,
            isActive = isActive,
            conditionsJson = conditionsJson,
            lastRun = lastRun,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    // Simple context for basic tests
    fun createSimpleContext(
        user: User = TestUsers.bidder,
        currentPrice: BigDecimal = BigDecimal("100.00"),
        minutesUntilEnd: Long = 120,
        currentHighestBid: Bid? = null,
        allBids: List<Bid> = emptyList()
    ): AutoBidContext {
        val currentTime = LocalDateTime.now()
        val auction = createAuction(
            expiredDate = currentTime.plusMinutes(minutesUntilEnd),
            minimumPrice = currentPrice,
            lastBid = currentPrice
        )
        val autoBid = createAutoBid(user = user, auction = auction)
        
        // If currentPrice > minimumPrice, create a highest bid so getCurrentPrice() works correctly
        val effectiveHighestBid = currentHighestBid ?: if (currentPrice > auction.minimumPrice) {
            createBid(
                id = 999,
                auction = auction,
                bidder = TestUsers.competitor1,
                value = currentPrice,
                timeStamp = currentTime.minusMinutes(10)
            )
        } else null
        
        val effectiveAllBids = if (effectiveHighestBid != null && allBids.isEmpty()) {
            listOf(effectiveHighestBid)
        } else {
            allBids
        }
        
        return AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = effectiveHighestBid,
            allBids = effectiveAllBids,
            currentTime = currentTime
        )
    }

    fun createContextWithHighestBid(
        user: User = TestUsers.bidder,
        highestBidder: User = TestUsers.competitor1,
        currentPrice: BigDecimal = BigDecimal("150.00"),
        minutesUntilEnd: Long = 120
    ): AutoBidContext {
        val currentTime = LocalDateTime.now()
        val auction = createAuction(
            expiredDate = currentTime.plusMinutes(minutesUntilEnd),
            lastBid = currentPrice
        )
        val autoBid = createAutoBid(user = user, auction = auction)
        
        val highestBid = createBid(
            id = 1,
            auction = auction,
            bidder = highestBidder,
            value = currentPrice,
            timeStamp = currentTime.minusMinutes(5),
            isWinning = true
        )
        
        val lastBidByUser = if (highestBidder.id == user.id) highestBid else null
        
        return AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = highestBid,
            allBids = listOf(highestBid),
            currentTime = currentTime,
            lastBidByThisAutoBid = lastBidByUser
        )
    }

    // Creates context with bid history - bidValues in reverse chronological order
    fun createContextWithBidHistory(
        user: User = TestUsers.bidder,
        bidValues: List<BigDecimal>,
        bidders: List<User>? = null,
        minutesUntilEnd: Long = 120
    ): AutoBidContext {
        val currentTime = LocalDateTime.now()
        val auction = createAuction(
            expiredDate = currentTime.plusMinutes(minutesUntilEnd)
        )
        val autoBid = createAutoBid(user = user, auction = auction)
        
        val effectiveBidders = bidders ?: bidValues.mapIndexed { index, _ ->
            if (index % 2 == 0) TestUsers.competitor1 else user
        }
        
        val bids = bidValues.mapIndexed { index, value ->
            createBid(
                id = index + 1,
                auction = auction,
                bidder = effectiveBidders[index],
                value = value,
                timeStamp = currentTime.minusMinutes((bidValues.size - index).toLong()),
                isWinning = index == 0
            )
        }
        
        val lastBidByUser = bids.firstOrNull { it.bidder.id == user.id }
        
        return AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = bids.firstOrNull(),
            allBids = bids,
            currentTime = currentTime,
            lastBidByThisAutoBid = lastBidByUser
        )
    }

    fun createContextWithExactTime(
        user: User = TestUsers.bidder,
        currentTime: LocalDateTime,
        expiredDate: LocalDateTime,
        lastBidTime: LocalDateTime? = null,
        currentPrice: BigDecimal = BigDecimal("150.00")
    ): AutoBidContext {
        val auction = createAuction(
            expiredDate = expiredDate,
            lastBid = currentPrice
        )
        val autoBid = createAutoBid(user = user, auction = auction)
        
        val lastBid = lastBidTime?.let {
            createBid(
                id = 1,
                auction = auction,
                bidder = TestUsers.competitor1,
                value = currentPrice,
                timeStamp = it,
                isWinning = true
            )
        }
        
        return AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = user,
            currentHighestBid = lastBid,
            allBids = if (lastBid != null) listOf(lastBid) else emptyList(),
            currentTime = currentTime
        )
    }
}
