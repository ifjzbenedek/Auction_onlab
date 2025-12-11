package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.DTOs.AgentDTOs.ChatMessageDTO
import org.example.bidverse_backend.Exceptions.AuctionNotFoundException
import org.example.bidverse_backend.Exceptions.AutoBidAlreadyExistsException
import org.example.bidverse_backend.Exceptions.LLMServiceException
import org.example.bidverse_backend.Exceptions.UserNotFoundException
import org.example.bidverse_backend.services.AgentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/agent")
class AgentController(private val agentService: AgentService) {

    data class AgentChatRequest(
        val auctionId: Int,
        val messages: List<ChatMessageDTO>
    )

    @PostMapping("/chat")
    fun processChat(@RequestBody request: AgentChatRequest): ResponseEntity<Any> {
        return try {
            val autobidConfig = agentService.processChat(request.auctionId, request.messages)
            ResponseEntity.status(HttpStatus.CREATED).body(autobidConfig)
        } catch (e: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: AuctionNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: AutoBidAlreadyExistsException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
        } catch (e: LLMServiceException) {
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing chat: ${e.message}")
        }
    }
}
