package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.Exceptions.AutoBidNotFoundException
import org.example.bidverse_backend.autobid.AutoBidExecutorService
import org.example.bidverse_backend.autobid.AutoBidExecutionResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/autobid")
class AutoBidController(
    private val autoBidExecutorService: AutoBidExecutorService
) {

    @PostMapping("/{autoBidId}/execute")
    fun executeAutoBid(@PathVariable autoBidId: Int): ResponseEntity<Any> {
        return try {
            val result = autoBidExecutorService.executeAutoBid(autoBidId)
            
            when (result) {
                is AutoBidExecutionResult.BidPlaced -> {
                    ResponseEntity.ok(mapOf(
                        "status" to "success",
                        "action" to "bid_placed",
                        "amount" to result.amount,
                        "bidId" to result.bidId,
                        "reason" to result.reason
                    ))
                }
                is AutoBidExecutionResult.Skipped -> {
                    ResponseEntity.ok(mapOf(
                        "status" to "skipped",
                        "reason" to result.reason
                    ))
                }
                is AutoBidExecutionResult.Stopped -> {
                    ResponseEntity.ok(mapOf(
                        "status" to "stopped",
                        "reason" to result.reason
                    ))
                }
                is AutoBidExecutionResult.NotFound -> {
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("AutoBid not found")
                }
                is AutoBidExecutionResult.Error -> {
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.reason)
                }
            }
        } catch (e: AutoBidNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error executing autobid: ${e.message}")
        }
    }

    @PostMapping("/execute-all")
    fun executeAllDueAutoBids(): ResponseEntity<Any> {
        return try {
            val summaries = autoBidExecutorService.executeAllDueAutoBids()
            
            val response = mapOf(
                "total" to summaries.size,
                "executed" to summaries.count { it.result is AutoBidExecutionResult.BidPlaced },
                "skipped" to summaries.count { it.result is AutoBidExecutionResult.Skipped },
                "stopped" to summaries.count { it.result is AutoBidExecutionResult.Stopped },
                "errors" to summaries.count { it.result is AutoBidExecutionResult.Error },
                "details" to summaries.map { summary ->
                    mapOf(
                        "autoBidId" to summary.autoBidId,
                        "auctionId" to summary.auctionId,
                        "userId" to summary.userId,
                        "result" to when (val r = summary.result) {
                            is AutoBidExecutionResult.BidPlaced -> "BID_PLACED: ${r.amount}"
                            is AutoBidExecutionResult.Skipped -> "SKIPPED: ${r.reason}"
                            is AutoBidExecutionResult.Stopped -> "STOPPED: ${r.reason}"
                            is AutoBidExecutionResult.NotFound -> "NOT_FOUND"
                            is AutoBidExecutionResult.Error -> "ERROR: ${r.reason}"
                        }
                    )
                }
            )
            
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error executing autobids: ${e.message}")
        }
    }
}
