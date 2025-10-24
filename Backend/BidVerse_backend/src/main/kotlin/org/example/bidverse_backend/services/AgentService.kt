package org.example.bidverse_backend.services

import org.example.bidverse_backend.DTOs.AgentDTOs.AutoBidAgentConfigDTO
import org.example.bidverse_backend.DTOs.AgentDTOs.AgentProcessResponseDTO
import org.example.bidverse_backend.DTOs.AgentDTOs.ChatMessageDTO
import org.example.bidverse_backend.DTOs.EntityToDTO.toAutobidAgentConfigDTO
import org.example.bidverse_backend.Exceptions.AuctionNotFoundException
import org.example.bidverse_backend.Exceptions.AutoBidAlreadyExistsException
import org.example.bidverse_backend.Exceptions.LLMServiceException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.Security.SecurityUtils
import org.example.bidverse_backend.entities.AutoBid
import org.example.bidverse_backend.repositories.AuctionRepository
import org.example.bidverse_backend.repositories.AutoBidRepository
import org.example.bidverse_backend.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.RestClientException
import java.time.LocalDateTime

@Service
class AgentService(
    private val autoBidRepository: AutoBidRepository,
    private val userRepository: UserRepository,
    private val auctionRepository: AuctionRepository,
    private val securityUtils: SecurityUtils,
    private val restTemplate: RestTemplate
) {

    fun processChat(auctionId: Int, messages: List<ChatMessageDTO>): AgentProcessResponseDTO {
        // Get current logged-in user
        val currentUserId = securityUtils.getCurrentUserId()
        val user = userRepository.findById(currentUserId)
            .orElseThrow { UserNotFoundException("Current user not found.") }

        // Validate auction exists FIRST
        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { AuctionNotFoundException("Auction with id $auctionId not found.") }

        // Check if autobid already exists for this user and auction
        val existingAutoBid = autoBidRepository.findByUserIdAndAuctionId(currentUserId, auctionId)
        if (existingAutoBid != null) {
            throw AutoBidAlreadyExistsException("AutoBid already exists for this auction.")
        }

        // Call LLM microservice - AI NO LONGER needs to extract auctionId
        val llmServiceUrl = "http://localhost:5002/agent/process"
        val agentProcessResponse: AgentProcessResponseDTO = try {
            restTemplate.postForObject(llmServiceUrl, messages, AgentProcessResponseDTO::class.java)
                ?: throw LLMServiceException("LLM service returned null response")
        } catch (e: RestClientException) {
            throw LLMServiceException("Failed to communicate with LLM service: ${e.message}")
        }

        // If configuration is incomplete, return the response without saving
        if (!agentProcessResponse.isComplete) {
            return agentProcessResponse
        }

        val llmConfig = agentProcessResponse.config

        // IMPORTANT: Use auctionId from frontend, NOT from AI extraction
        // The AI is NOT responsible for extracting auctionId anymore

        // IMPORTANT: Use auctionId from frontend, NOT from AI extraction
        // The AI is NOT responsible for extracting auctionId anymore

        // Create AutoBid entity from LLM response + frontend auctionId
        val autoBid = AutoBid(
            id = null,
            user = user,
            auction = auction, // Use auction from frontend auctionId
            maxBidAmount = llmConfig.maxBidAmount,
            startingBidAmount = llmConfig.startingBidAmount,
            incrementAmount = llmConfig.incrementAmount,
            intervalMinutes = llmConfig.intervalMinutes,
            isActive = llmConfig.isActive,
            conditionsJson = llmConfig.conditionsJson,
            nextRun = llmConfig.intervalMinutes?.let { LocalDateTime.now().plusMinutes(it.toLong()) },
            lastRun = null,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )

        // Save to database
        val savedAutoBid = autoBidRepository.save(autoBid)
        
        // Return success response with saved config (update auctionId to the actual one used)
        val finalConfig = savedAutoBid.toAutobidAgentConfigDTO()
        return AgentProcessResponseDTO(
            config = finalConfig.copy(auctionId = auctionId), // Ensure we return the correct auctionId
            agentResponse = "Autobid successfully created and activated!",
            isComplete = true,
            needsMoreInfo = false
        )
    }
}
