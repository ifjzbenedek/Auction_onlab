package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TargetUserIdsConditionTest {

    private val condition = TargetUserIdsCondition()

    @Test
    fun `should return true when condition is not configured (null)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `should return true when highest bidder is in target list`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        
        val targetIds = listOf(3, 4, 5) // competitor1 is id=3
        assertTrue(condition.shouldBid(context, targetIds))
    }

    @Test
    fun `should return false when highest bidder is not in target list`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        
        val targetIds = listOf(5, 6, 7) // competitor1 (id=3) is not in list
        assertFalse(condition.shouldBid(context, targetIds))
    }

    @Test
    fun `should return false when no bids yet (no highest bidder)`() {
        val context = ConditionTestHelpers.createSimpleContext()
        
        val targetIds = listOf(3, 4, 5)
        assertFalse(condition.shouldBid(context, targetIds))
    }

    @Test
    fun `should return true for single target user`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.friend, // id=5
            currentPrice = BigDecimal("150.00")
        )
        
        val targetIds = listOf(5)
        assertTrue(condition.shouldBid(context, targetIds))
    }

    @Test
    fun `should return false when empty target list`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        
        assertFalse(condition.shouldBid(context, emptyList<Int>()))
    }

    @Test
    fun `should work with multiple target users`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            user = TestUsers.bidder,
            highestBidder = TestUsers.competitor2, // id=4
            currentPrice = BigDecimal("150.00")
        )
        
        val targetIds = listOf(3, 4, 5) // competitor2 is id=4
        assertTrue(condition.shouldBid(context, targetIds))
    }
}
