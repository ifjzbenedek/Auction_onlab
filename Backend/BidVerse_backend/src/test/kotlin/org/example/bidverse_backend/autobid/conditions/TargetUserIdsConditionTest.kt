package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TargetUserIdsConditionTest {

    private val condition = TargetUserIdsCondition()

    @Test
    fun `null config allows bidding`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `only bids against target users`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        
        assertTrue(condition.shouldBid(context, listOf(3, 4, 5)))
        assertFalse(condition.shouldBid(context, listOf(5, 6, 7)))
    }

    @Test
    fun `does not bid when no current bidder`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertFalse(condition.shouldBid(context, listOf(3, 4, 5)))
    }

    @Test
    fun `works with single target`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.friend,
            currentPrice = BigDecimal("150.00")
        )
        
        assertTrue(condition.shouldBid(context, listOf(5)))
        assertFalse(condition.shouldBid(context, emptyList<Int>()))
    }

    @Test
    fun `handles multiple targets`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor2,
            currentPrice = BigDecimal("150.00")
        )
        
        assertTrue(condition.shouldBid(context, listOf(3, 4, 5)))
    }
}
