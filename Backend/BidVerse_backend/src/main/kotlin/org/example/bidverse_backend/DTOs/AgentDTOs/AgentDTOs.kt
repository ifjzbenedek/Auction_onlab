package org.example.bidverse_backend.DTOs.AgentDTOs

import java.math.BigDecimal

data class ChatMessageDTO(
    val role: String,
    val content: String
)

data class AutoBidAgentConfigDTO(
    val id: Int,
    val auctionId: Int,
    val userId: Int,
    val maxBidAmount: BigDecimal?,
    val incrementAmount: BigDecimal?,
    val intervalMinutes: Int?,
    val isActive: Boolean,
    val conditionsJson: Map<String, Any>? = null
)

data class AgentProcessResponseDTO(
    val config: AutoBidAgentConfigDTO,
    val agentResponse: String,
    val isComplete: Boolean,
    val needsMoreInfo: Boolean
)
