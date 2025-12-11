package org.example.bidverse_backend.services

import org.example.bidverse_backend.Exceptions.*
import org.example.bidverse_backend.repositories.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import org.example.bidverse_backend.Security.SecurityUtils

/**
 * Tests for AI-powered features in AuctionService
 * - generateAuctionDescription (AI Flask microservice)
 * - smartSearch (AI Search microservice)
 * - indexAuctionForSearch (AI Search microservice)
 */
class AuctionServiceAITest {

    private lateinit var auctionRepository: AuctionRepository
    private lateinit var userRepository: UserRepository
    private lateinit var bidRepository: BidRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var securityUtils: SecurityUtils
    private lateinit var restTemplate: RestTemplate
    private lateinit var imageService: ImageService
    private lateinit var imageRepository: ImageRepository
    private lateinit var auctionService: AuctionService

    @BeforeEach
    fun setup() {
        auctionRepository = mock(AuctionRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        bidRepository = mock(BidRepository::class.java)
        categoryRepository = mock(CategoryRepository::class.java)
        securityUtils = mock(SecurityUtils::class.java)
        restTemplate = mock(RestTemplate::class.java)
        imageService = mock(ImageService::class.java)
        imageRepository = mock(ImageRepository::class.java)
        
        auctionService = AuctionService(
            auctionRepository,
            userRepository,
            bidRepository,
            categoryRepository,
            securityUtils,
            restTemplate,
            imageService,
            imageRepository
        )
    }
    
    @Test
    fun `generateAuctionDescription should call AI service and return description`() {
        // Mock image files
        val image1 = mock(MultipartFile::class.java)
        val image2 = mock(MultipartFile::class.java)
        `when`(image1.isEmpty).thenReturn(false)
        `when`(image2.isEmpty).thenReturn(false)
        `when`(image1.contentType).thenReturn("image/jpeg")
        `when`(image2.contentType).thenReturn("image/png")
        `when`(image1.originalFilename).thenReturn("test1.jpg")
        `when`(image2.originalFilename).thenReturn("test2.png")
        `when`(image1.resource).thenReturn(mock(org.springframework.core.io.Resource::class.java))
        `when`(image2.resource).thenReturn(mock(org.springframework.core.io.Resource::class.java))
        
        val expectedResponse = """{"description": "Beautiful vintage watch", "itemState": "Like new", "condition": 95}"""
        
        `when`(restTemplate.postForObject(
            eq("http://localhost:5000/generate-description"),
            any(HttpEntity::class.java),
            eq(String::class.java)
        )).thenReturn(expectedResponse)
        
        // Execute
        val result = auctionService.generateAuctionDescription(listOf(image1, image2))
        
        // Verify
        assertNotNull(result)
        assertTrue(result.contains("Beautiful vintage watch"))
        verify(restTemplate).postForObject(
            eq("http://localhost:5000/generate-description"),
            any(HttpEntity::class.java),
            eq(String::class.java)
        )
    }

    @Test
    fun `generateAuctionDescription should throw when images list is empty`() {
        assertThrows(DescriptionGenerationException::class.java) {
            auctionService.generateAuctionDescription(emptyList())
        }
    }

    @Test
    fun `generateAuctionDescription should throw when all images are empty`() {
        val emptyImage = mock(MultipartFile::class.java)
        `when`(emptyImage.isEmpty).thenReturn(true)
        
        assertThrows(DescriptionGenerationException::class.java) {
            auctionService.generateAuctionDescription(listOf(emptyImage))
        }
    }

    @Test
    fun `generateAuctionDescription should throw when image has invalid filename`() {
        val invalidImage = mock(MultipartFile::class.java)
        `when`(invalidImage.isEmpty).thenReturn(false)
        `when`(invalidImage.contentType).thenReturn("text/plain")
        
        assertThrows(DescriptionGenerationException::class.java) {
            auctionService.generateAuctionDescription(listOf(invalidImage))
        }
    }

    @Test
    fun `generateAuctionDescription should throw when AI service returns null`() {
        val image = mock(MultipartFile::class.java)
        `when`(image.isEmpty).thenReturn(false)
        `when`(image.contentType).thenReturn("image/jpeg")
        `when`(image.originalFilename).thenReturn("test.jpg")
        `when`(image.resource).thenReturn(mock(org.springframework.core.io.Resource::class.java))
        
        `when`(restTemplate.postForObject(
            eq("http://localhost:5000/generate-description"),
            any(HttpEntity::class.java),
            eq(String::class.java)
        )).thenReturn(null)
        
        assertThrows(IllegalStateException::class.java) {
            auctionService.generateAuctionDescription(listOf(image))
        }
    }

    @Test
    fun `smartSearch should call AI service and return auction cards`() {
        val query = "vintage watches under 10000"
        val mockResponse = mapOf(
            "auction_ids" to listOf("1", "2", "3")
        )
        
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenReturn(mockResponse)
        
        // Mock auctions
        val auction1 = createMockAuction(1, "Vintage Rolex")
        val auction2 = createMockAuction(2, "Old Omega")
        val auction3 = createMockAuction(3, "Antique Seiko")
        
        `when`(auctionRepository.findByIdIn(listOf(1, 2, 3)))
            .thenReturn(listOf(auction1, auction2, auction3))
        
        // Execute
        val results = auctionService.smartSearch(query)
        
        // Verify
        assertEquals(3, results.size)
        verify(restTemplate).postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )
    }

    @Test
    fun `smartSearch should throw when query is blank`() {
        assertThrows(InvalidSearchQueryException::class.java) {
            auctionService.smartSearch("")
        }
        
        assertThrows(InvalidSearchQueryException::class.java) {
            auctionService.smartSearch("   ")
        }
    }

    @Test
    fun `smartSearch should throw timeout exception on ResourceAccessException`() {
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenThrow(ResourceAccessException("Connection timeout"))
        
        assertThrows(SearchServiceTimeoutException::class.java) {
            auctionService.smartSearch("test query")
        }
    }

    @Test
    fun `smartSearch should throw unavailable exception on HttpClientErrorException`() {
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"))
        
        assertThrows(SearchServiceUnavailableException::class.java) {
            auctionService.smartSearch("test query")
        }
    }

    @Test
    fun `smartSearch should throw unavailable exception on HttpServerErrorException`() {
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenThrow(HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"))
        
        assertThrows(SearchServiceUnavailableException::class.java) {
            auctionService.smartSearch("test query")
        }
    }

    @Test
    fun `smartSearch should throw when service returns null`() {
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenReturn(null)
        
        assertThrows(SearchServiceUnavailableException::class.java) {
            auctionService.smartSearch("test query")
        }
    }

    @Test
    fun `smartSearch should filter out invalid auction IDs`() {
        val mockResponse = mapOf(
            "auction_ids" to listOf("1", "invalid", "2", "999")
        )
        
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenReturn(mockResponse)
        
        val auction1 = createMockAuction(1, "Item 1")
        val auction2 = createMockAuction(2, "Item 2")
        
        // findByIdIn only returns existing auctions
        `when`(auctionRepository.findByIdIn(listOf(1, 2, 999)))
            .thenReturn(listOf(auction1, auction2))
        
        val results = auctionService.smartSearch("test")
        
        assertEquals(2, results.size)
    }

    @Test
    fun `smartSearch should return empty list when no auction IDs returned`() {
        val mockResponse = mapOf(
            "auction_ids" to emptyList<String>()
        )
        
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenReturn(mockResponse)
        
        val results = auctionService.smartSearch("nonexistent query")
        
        assertEquals(0, results.size)
    }

    @Test
    fun `smartSearch should return empty list when auction_ids is null`() {
        val mockResponse = mapOf(
            "other_field" to "value"
        )
        
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenReturn(mockResponse)
        
        val results = auctionService.smartSearch("test")
        
        assertEquals(0, results.size)
    }

    @Test
    fun `smartSearch should preserve order of auction IDs from AI service`() {
        val mockResponse = mapOf(
            "auction_ids" to listOf("3", "1", "2")
        )
        
        `when`(restTemplate.postForObject(
            eq("http://localhost:8001/search"),
            any(HttpEntity::class.java),
            eq(Map::class.java)
        )).thenReturn(mockResponse)
        
        val auction1 = createMockAuction(1, "Item 1")
        val auction2 = createMockAuction(2, "Item 2")
        val auction3 = createMockAuction(3, "Item 3")
        
        `when`(auctionRepository.findByIdIn(listOf(3, 1, 2)))
            .thenReturn(listOf(auction1, auction2, auction3))
        
        val results = auctionService.smartSearch("test")
        
        // Should maintain AI service order: 3, 1, 2
        assertEquals(3, results.size)
        assertEquals(3, results[0].id)
        assertEquals(1, results[1].id)
        assertEquals(2, results[2].id)
    }

    private fun createMockAuction(id: Int, itemName: String): org.example.bidverse_backend.entities.Auction {
        val owner = org.example.bidverse_backend.entities.User(
            id = 1, userName = "Owner", emailAddress = "owner@test.com",
            phoneNumber = "1234567890", role = "USER"
        )
        val category = org.example.bidverse_backend.entities.Category(id = 1, categoryName = "Watches")
        
        return org.example.bidverse_backend.entities.Auction(
            id = id,
            owner = owner,
            category = category,
            itemName = itemName,
            minimumPrice = java.math.BigDecimal("100.00"),
            createDate = java.time.LocalDateTime.now().minusDays(1),
            expiredDate = java.time.LocalDateTime.now().plusDays(7),
            lastBid = null,
            description = "Test auction",
            type = "STANDARD",
            extraTime = null,
            itemState = "NEW",
            tags = null,
            minStep = 10,
            condition = 1,
            startDate = java.time.LocalDateTime.now().minusHours(1)
        )
    }
}
