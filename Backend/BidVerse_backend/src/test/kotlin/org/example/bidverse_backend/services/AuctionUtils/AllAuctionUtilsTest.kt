package org.example.bidverse_backend.services.AuctionUtils

import org.example.bidverse_backend.entities.Auction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

/**
 * Unit tests for AllAuctionUtils - auction status calculation logic.
 * These are pure utility functions with no dependencies, making them ideal for unit testing.
 */
class AllAuctionUtilsTest {

    @Test
    fun `calculateStatus should return UPCOMING when before startDate`() {
        // Given
        val startDate = LocalDateTime.now().plusHours(2)
        val expiredDate = LocalDateTime.now().plusDays(1)
        
        // When
        val status = AllAuctionUtils.calculateStatus(startDate, expiredDate)
        
        // Then
        assertEquals("UPCOMING", status)
    }

    @Test
    fun `calculateStatus should return ACTIVE when between start and end dates`() {
        // Given
        val startDate = LocalDateTime.now().minusHours(1)
        val expiredDate = LocalDateTime.now().plusHours(2)
        
        // When
        val status = AllAuctionUtils.calculateStatus(startDate, expiredDate)
        
        // Then
        assertEquals("ACTIVE", status)
    }

    @Test
    fun `calculateStatus should return CLOSED when after expiredDate`() {
        // Given
        val startDate = LocalDateTime.now().minusDays(2)
        val expiredDate = LocalDateTime.now().minusHours(1)
        
        // When
        val status = AllAuctionUtils.calculateStatus(startDate, expiredDate)
        
        // Then
        assertEquals("CLOSED", status)
    }

    @Test
    fun `calculateStatus should return ACTIVE immediately when startDate is null`() {
        // Given
        val startDate: LocalDateTime? = null
        val expiredDate = LocalDateTime.now().plusHours(2)
        
        // When
        val status = AllAuctionUtils.calculateStatus(startDate, expiredDate)
        
        // Then
        assertEquals("ACTIVE", status)
    }

    @Test
    fun `calculateStatus should return CLOSED when startDate is null but expired`() {
        // Given
        val startDate: LocalDateTime? = null
        val expiredDate = LocalDateTime.now().minusHours(1)
        
        // When
        val status = AllAuctionUtils.calculateStatus(startDate, expiredDate)
        
        // Then
        assertEquals("CLOSED", status)
    }

    @Test
    fun `calculateStatus should handle boundary condition exactly at startDate`() {
        // Given
        val now = LocalDateTime.now()
        val startDate = now
        val expiredDate = now.plusHours(2)
        
        // When
        val status = AllAuctionUtils.calculateStatus(startDate, expiredDate)
        
        // Then
        // At exactly startDate, it should be ACTIVE (not UPCOMING)
        assertEquals("ACTIVE", status)
    }

    @Test
    fun `calculateStatus should handle boundary condition exactly at expiredDate`() {
        // Given
        val now = LocalDateTime.now()
        val startDate = now.minusHours(2)
        val expiredDate = now
        
        // When
        val status = AllAuctionUtils.calculateStatus(startDate, expiredDate)
        
        // Then
        // At exactly expiredDate, it should be CLOSED (not ACTIVE)
        assertEquals("CLOSED", status)
    }

    @Test
    fun `isAuctionActive should return true when status is ACTIVE`() {
        // Given
        val startDate = LocalDateTime.now().minusHours(1)
        val expiredDate = LocalDateTime.now().plusHours(2)
        
        // When
        val isActive = AllAuctionUtils.isAuctionActive(startDate, expiredDate)
        
        // Then
        assertTrue(isActive)
    }

    @Test
    fun `isAuctionActive should return false when status is UPCOMING`() {
        // Given
        val startDate = LocalDateTime.now().plusHours(1)
        val expiredDate = LocalDateTime.now().plusDays(1)
        
        // When
        val isActive = AllAuctionUtils.isAuctionActive(startDate, expiredDate)
        
        // Then
        assertFalse(isActive)
    }

    @Test
    fun `isAuctionActive should return false when status is CLOSED`() {
        // Given
        val startDate = LocalDateTime.now().minusDays(2)
        val expiredDate = LocalDateTime.now().minusHours(1)
        
        // When
        val isActive = AllAuctionUtils.isAuctionActive(startDate, expiredDate)
        
        // Then
        assertFalse(isActive)
    }

    @Test
    fun `isAuctionActive should return true when startDate is null and not expired`() {
        // Given
        val startDate: LocalDateTime? = null
        val expiredDate = LocalDateTime.now().plusHours(2)
        
        // When
        val isActive = AllAuctionUtils.isAuctionActive(startDate, expiredDate)
        
        // Then
        assertTrue(isActive)
    }
}
