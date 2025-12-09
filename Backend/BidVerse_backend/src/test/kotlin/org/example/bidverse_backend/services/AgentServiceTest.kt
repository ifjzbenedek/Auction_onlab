package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.AgentDTOs.AutoBidAgentConfigDTO
import org.example.bidverse_backend.DTOs.AgentDTOs.AgentProcessResponseDTO
import org.example.bidverse_backend.DTOs.AgentDTOs.ChatMessageDTO
import org.example.bidverse_backend.Exceptions.*
import org.example.bidverse_backend.Security.SecurityUtils
import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.entities.AutoBid
import org.example.bidverse_backend.entities.Category
import org.example.bidverse_backend.entities.User
import org.example.bidverse_backend.repositories.AuctionRepository
import org.example.bidverse_backend.repositories.AutoBidRepository
import org.example.bidverse_backend.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * Tests for AI Agent integration in AgentService
 * - processChat (AI Agent microservice)
 * 
 * The AI Agent uses Gemini LLM to extract AutoBid configuration
 * from natural language conversations.
 */
class AgentServiceTest {

    private lateinit var autoBidRepository: AutoBidRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auctionRepository: AuctionRepository
    private lateinit var securityUtils: SecurityUtils
    private lateinit var restTemplate: RestTemplate
    private lateinit var agentService: AgentService

    private lateinit var testUser: User
    private lateinit var testAuction: Auction

    @BeforeEach
    fun setup() {
        autoBidRepository = mock(AutoBidRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        auctionRepository = mock(AuctionRepository::class.java)
        securityUtils = mock(SecurityUtils::class.java)
        restTemplate = mock(RestTemplate::class.java)
        
        agentService = AgentService(
            autoBidRepository,
            userRepository,
            auctionRepository,
            securityUtils,
            restTemplate
        )

        // Setup test entities
        testUser = User(
            id = 2,
            userName = "TestUser",
            emailAddress = "test@example.com",
            phoneNumber = "1234567890",
            role = "USER"
        )

        val owner = User(
            id = 1,
            userName = "Owner",
            emailAddress = "owner@example.com",
            phoneNumber = "0987654321",
            role = "USER"
        )

        val category = Category(id = 1, categoryName = "Electronics")

        testAuction = Auction(
            id = 100,
            owner = owner,
            category = category,
            itemName = "Vintage Camera",
            minimumPrice = BigDecimal("500.00"),
            createDate = LocalDateTime.now().minusDays(1),
            expiredDate = LocalDateTime.now().plusDays(7),
            lastBid = null,
            description = "Rare vintage camera",
            type = "STANDARD",
            extraTime = null,
            itemState = "GOOD",
            tags = "vintage,camera",
            minStep = 50,
            condition = 8,
            startDate = LocalDateTime.now().minusHours(1)
        )

        // Default mocks
        `when`(securityUtils.getCurrentUserId()).thenReturn(2)
        `when`(userRepository.findById(2)).thenReturn(Optional.of(testUser))
        `when`(auctionRepository.findById(100)).thenReturn(Optional.of(testAuction))
        `when`(autoBidRepository.findByUserIdAndAuctionId(2, 100)).thenReturn(null)
    }

    // ==================== Successful Scenarios ====================

    @Test
    fun `processChat should create autobid when LLM returns complete config`() {
        val messages = listOf(
            ChatMessageDTO(role = "user", content = "I want to autobid on auction 100"),
            ChatMessageDTO(role = "assistant", content = "What's your max bid?"),
            ChatMessageDTO(role = "user", content = "Max 5000, increase by 100 every 30 minutes")
        )

        val llmConfig = AutoBidAgentConfigDTO(
            id = 0,
            auctionId = 0,
            userId = 0,
            maxBidAmount = BigDecimal("5000.00"),
            startingBidAmount = null,
            incrementAmount = BigDecimal("100.00"),
            intervalMinutes = 30,
            isActive = true,
            conditionsJson = null
        )

        val llmResponse = AgentProcessResponseDTO(
            config = llmConfig,
            agentResponse = "Great! I've configured your autobid.",
            isComplete = true,
            needsMoreInfo = false
        )

        `when`(restTemplate.postForObject(
            eq("http://localhost:5002/agent/process"),
            eq(messages),
            eq(AgentProcessResponseDTO::class.java)
        )).thenReturn(llmResponse)

        val savedAutoBid = AutoBid(
            id = 1,
            user = testUser,
            auction = testAuction,
            maxBidAmount = BigDecimal("5000.00"),
            startingBidAmount = null,
            incrementAmount = BigDecimal("100.00"),
            intervalMinutes = 30,
            isActive = true,
            conditionsJson = null,
            nextRun = LocalDateTime.now().plusMinutes(30),
            lastRun = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        `when`(autoBidRepository.save(any(AutoBid::class.java))).thenReturn(savedAutoBid)

        // Execute
        val result = agentService.processChat(100, messages)

        // Verify
        assertTrue(result.isComplete)
        assertFalse(result.needsMoreInfo)
        assertEquals(100, result.config.auctionId)
        assertEquals(BigDecimal("5000.00"), result.config.maxBidAmount)
        assertEquals(BigDecimal("100.00"), result.config.incrementAmount)
        assertEquals(30, result.config.intervalMinutes)
        
        verify(autoBidRepository).save(any(AutoBid::class.java))
        verify(restTemplate).postForObject(
            eq("http://localhost:5002/agent/process"),
            eq(messages),
            eq(AgentProcessResponseDTO::class.java)
        )
    }

    @Test
    fun `processChat should return incomplete response when LLM needs more info`() {
        val messages = listOf(
            ChatMessageDTO(role = "user", content = "I want to set up autobidding")
        )

        val llmResponse = AgentProcessResponseDTO(
            config = AutoBidAgentConfigDTO(
                id = 0, auctionId = 0, userId = 0,
                maxBidAmount = null, startingBidAmount = null,
                incrementAmount = null, intervalMinutes = null,
                isActive = true, conditionsJson = null
            ),
            agentResponse = "Sure! What's the maximum amount you want to bid?",
            isComplete = false,
            needsMoreInfo = true
        )

        `when`(restTemplate.postForObject(
            eq("http://localhost:5002/agent/process"),
            eq(messages),
            eq(AgentProcessResponseDTO::class.java)
        )).thenReturn(llmResponse)

        // Execute
        val result = agentService.processChat(100, messages)

        // Verify
        assertFalse(result.isComplete)
        assertTrue(result.needsMoreInfo)
        verify(autoBidRepository, never()).save(any(AutoBid::class.java))
    }

    @Test
    fun `processChat should handle complex conditions from LLM`() {
        val messages = listOf(
            ChatMessageDTO(role = "user", content = "Set up autobid with active hours 9-17 and only if price below 3000")
        )

        val conditionsJson = mapOf(
            "active_hours" to listOf(9, 10, 11, 12, 13, 14, 15, 16, 17),
            "only_if_price_below" to 3000
        )

        val llmConfig = AutoBidAgentConfigDTO(
            id = 0, auctionId = 0, userId = 0,
            maxBidAmount = BigDecimal("3000.00"),
            startingBidAmount = null,
            incrementAmount = BigDecimal("50.00"),
            intervalMinutes = 10,
            isActive = true,
            conditionsJson = conditionsJson
        )

        val llmResponse = AgentProcessResponseDTO(
            config = llmConfig,
            agentResponse = "Configured with time restrictions!",
            isComplete = true,
            needsMoreInfo = false
        )

        `when`(restTemplate.postForObject(
            eq("http://localhost:5002/agent/process"),
            eq(messages),
            eq(AgentProcessResponseDTO::class.java)
        )).thenReturn(llmResponse)

        val savedAutoBid = AutoBid(
            id = 1, user = testUser, auction = testAuction,
            maxBidAmount = BigDecimal("3000.00"), startingBidAmount = null,
            incrementAmount = BigDecimal("50.00"), intervalMinutes = 10,
            isActive = true, conditionsJson = conditionsJson,
            nextRun = LocalDateTime.now().plusMinutes(10),
            lastRun = null, createdAt = LocalDateTime.now(), updatedAt = null
        )

        `when`(autoBidRepository.save(any(AutoBid::class.java))).thenReturn(savedAutoBid)

        // Execute
        val result = agentService.processChat(100, messages)

        // Verify
        assertTrue(result.isComplete)
        assertNotNull(result.config.conditionsJson)
        assertEquals(9, (result.config.conditionsJson?.get("active_hours") as List<*>).size)
        verify(autoBidRepository).save(any(AutoBid::class.java))
    }

    // ==================== Error Scenarios ====================

    @Test
    fun `processChat should throw when user not found`() {
        `when`(userRepository.findById(2)).thenReturn(Optional.empty())

        assertThrows(UserNotFoundException::class.java) {
            agentService.processChat(100, emptyList())
        }
    }

    @Test
    fun `processChat should throw when auction not found`() {
        `when`(auctionRepository.findById(100)).thenReturn(Optional.empty())

        assertThrows(AuctionNotFoundException::class.java) {
            agentService.processChat(100, emptyList())
        }
    }

    @Test
    fun `processChat should throw when autobid already exists`() {
        val existingAutoBid = AutoBid(
            id = 1, user = testUser, auction = testAuction,
            maxBidAmount = BigDecimal("1000.00"), startingBidAmount = null,
            incrementAmount = BigDecimal("50.00"), intervalMinutes = 10,
            isActive = true, conditionsJson = null,
            nextRun = LocalDateTime.now().plusMinutes(10),
            lastRun = null, createdAt = LocalDateTime.now(), updatedAt = null
        )

        `when`(autoBidRepository.findByUserIdAndAuctionId(2, 100)).thenReturn(existingAutoBid)

        assertThrows(AutoBidAlreadyExistsException::class.java) {
            agentService.processChat(100, emptyList())
        }
    }

    @Test
    fun `processChat should throw when LLM service is unavailable`() {
        val messages = listOf(
            ChatMessageDTO(role = "user", content = "Setup autobid")
        )

        `when`(restTemplate.postForObject(
            eq("http://localhost:5002/agent/process"),
            eq(messages),
            eq(AgentProcessResponseDTO::class.java)
        )).thenThrow(RestClientException("Connection refused"))

        assertThrows(LLMServiceException::class.java) {
            agentService.processChat(100, messages)
        }
    }

    @Test
    fun `processChat should throw when LLM service returns null`() {
        val messages = listOf(
            ChatMessageDTO(role = "user", content = "Setup autobid")
        )

        `when`(restTemplate.postForObject(
            eq("http://localhost:5002/agent/process"),
            eq(messages),
            eq(AgentProcessResponseDTO::class.java)
        )).thenReturn(null)

        assertThrows(LLMServiceException::class.java) {
            agentService.processChat(100, messages)
        }
    }

    @Test
    fun `processChat should use auctionId from frontend parameter not LLM response`() {
        val messages = listOf(
            ChatMessageDTO(role = "user", content = "Max 1000 every 15 minutes")
        )

        // LLM might extract wrong auctionId or null
        val llmConfig = AutoBidAgentConfigDTO(
            id = 0, auctionId = 0, userId = 0,  // Use 0 instead of null for non-nullable Int
            maxBidAmount = BigDecimal("1000.00"),
            startingBidAmount = null,
            incrementAmount = BigDecimal("50.00"),
            intervalMinutes = 15,
            isActive = true, conditionsJson = null
        )

        val llmResponse = AgentProcessResponseDTO(
            config = llmConfig,
            agentResponse = "Done!",
            isComplete = true,
            needsMoreInfo = false
        )

        `when`(restTemplate.postForObject(
            eq("http://localhost:5002/agent/process"),
            eq(messages),
            eq(AgentProcessResponseDTO::class.java)
        )).thenReturn(llmResponse)

        val savedAutoBid = AutoBid(
            id = 1, user = testUser, auction = testAuction,
            maxBidAmount = BigDecimal("1000.00"), startingBidAmount = null,
            incrementAmount = BigDecimal("50.00"), intervalMinutes = 15,
            isActive = true, conditionsJson = null,
            nextRun = LocalDateTime.now().plusMinutes(15),
            lastRun = null, createdAt = LocalDateTime.now(), updatedAt = null
        )

        `when`(autoBidRepository.save(any(AutoBid::class.java))).thenReturn(savedAutoBid)

        // Execute - note we pass auctionId=100 from frontend
        val result = agentService.processChat(100, messages)

        // Verify - should use 100, not 999 from LLM
        assertEquals(100, result.config.auctionId)
    }
}
