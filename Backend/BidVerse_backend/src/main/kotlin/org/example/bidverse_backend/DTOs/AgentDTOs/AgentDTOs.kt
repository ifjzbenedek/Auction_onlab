package org.example.bidverse_backend.DTOs.AgentDTOs

import java.math.BigDecimal

data class ChatMessageDTO(
    val role: String,
    val content: String
)

data class AutoBidAgentConfigDTO(
    val id: Int = 0,
    val auctionId: Int? = null,
    val userId: Int = 0,
    val maxBidAmount: BigDecimal? = null,
    val startingBidAmount: BigDecimal? = null,
    val incrementAmount: BigDecimal? = null,
    val intervalMinutes: Int? = null,
    val isActive: Boolean = true,
    val conditionsJson: Map<String, Any>? = null
)

data class AgentProcessResponseDTO(
    val config: AutoBidAgentConfigDTO,
    val agentResponse: String,
    val isComplete: Boolean,
    val needsMoreInfo: Boolean
)
