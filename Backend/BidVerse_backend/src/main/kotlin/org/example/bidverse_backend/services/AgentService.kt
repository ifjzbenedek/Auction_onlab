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

    fun processChat(messages: List<ChatMessageDTO>): AgentProcessResponseDTO {
        // Get current logged-in user
        val currentUserId = securityUtils.getCurrentUserId()
        val user = userRepository.findById(currentUserId)
            .orElseThrow { UserNotFoundException("Current user not found.") }

        // Call LLM microservice
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

        // Validate auction exists (only if complete)
        val auction = auctionRepository.findById(llmConfig.auctionId)
            .orElseThrow { AuctionNotFoundException("Auction with id ${llmConfig.auctionId} not found.") }

        // Check if autobid already exists for this user and auction
        val existingAutoBid = autoBidRepository.findByUserIdAndAuctionId(currentUserId, llmConfig.auctionId)
        if (existingAutoBid != null) {
            throw AutoBidAlreadyExistsException("AutoBid already exists for this auction.")
        }

        // Create AutoBid entity from LLM response
        val autoBid = AutoBid(
            id = null,
            user = user,
            auction = auction,
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
        
        // Return success response with saved config
        return AgentProcessResponseDTO(
            config = savedAutoBid.toAutobidAgentConfigDTO(),
            agentResponse = "Autobid successfully created and activated!",
            isComplete = true,
            needsMoreInfo = false
        )
    }
}
