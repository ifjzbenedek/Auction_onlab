package org.example.bidverse_backend.DTOs.EntityToDTO

import org.example.bidverse_backend.DTOs.AgentDTOs.AutoBidAgentConfigDTO
import org.example.bidverse_backend.entities.AutoBid

fun AutoBid.toAutobidAgentConfigDTO(): AutoBidAgentConfigDTO {
    return AutoBidAgentConfigDTO(
        id = this.id!!,
        auctionId = this.auction.id!!,
        userId = this.user.id!!,
        maxBidAmount = this.maxBidAmount,
        incrementAmount = this.incrementAmount,
        intervalMinutes = this.intervalMinutes,
        isActive = this.isActive,
        conditionsJson = this.conditionsJson
    )
}