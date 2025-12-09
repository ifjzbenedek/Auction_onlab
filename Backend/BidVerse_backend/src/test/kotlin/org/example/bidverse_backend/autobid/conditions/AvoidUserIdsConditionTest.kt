package org.example.bidverse_backend.autobid.conditions

import org.example.bidverse_backend.autobid.conditions.ConditionTestHelpers.TestUsers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class AvoidUserIdsConditionTest {

    private val condition = AvoidUserIdsCondition()

    @Test
    fun `shouldBid should return true when conditionValue is null`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        assertTrue(condition.shouldBid(context, null))
    }

    @Test
    fun `shouldBid should return true when conditionValue is not a list`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        assertTrue(condition.shouldBid(context, "not a list"))
    }

    @Test
    fun `shouldBid should return false when current bidder is in avoid list`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        val avoidList = listOf(3, 5, 7)
        assertFalse(condition.shouldBid(context, avoidList))
    }

    @Test
    fun `shouldBid should return true when current bidder is not in avoid list`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        val avoidList = listOf(4, 6, 8)
        assertTrue(condition.shouldBid(context, avoidList))
    }

    @Test
    fun `shouldBid should return true when there is no current highest bid`() {
        val context = ConditionTestHelpers.createSimpleContext()
        val avoidList = listOf(3, 5, 7)
        assertTrue(condition.shouldBid(context, avoidList))
    }

    @Test
    fun `shouldBid should handle empty avoid list`() {
        val context = ConditionTestHelpers.createContextWithHighestBid(
            highestBidder = TestUsers.competitor1,
            currentPrice = BigDecimal("150.00")
        )
        val avoidList = emptyList<Int>()
        assertTrue(condition.shouldBid(context, avoidList))
    }
}
