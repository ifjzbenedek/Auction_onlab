package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class AvoidUserIdsConditionTest {

    private val condition = AvoidUserIdsCondition()

    @Test
    fun `null or invalid config returns true`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        assertTrue(condition.shouldBid(context, null))
        assertTrue(condition.shouldBid(context, "not a list"))
    }

    @Test
    fun `avoids bidding against specific users`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        
        assertFalse(condition.shouldBid(context, listOf(3, 5, 7)))
        assertTrue(condition.shouldBid(context, listOf(4, 6, 8)))
    }

    @Test
    fun `bids when no current highest bid`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, listOf(3, 5, 7)))
    }

    @Test
    fun `empty avoid list allows all bids`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        assertTrue(condition.shouldBid(context, emptyList<Int>()))
    }
}
