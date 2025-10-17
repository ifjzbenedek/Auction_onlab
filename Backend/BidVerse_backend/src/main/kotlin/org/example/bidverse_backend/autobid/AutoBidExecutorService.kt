package org.example.bidverse_backend.autobid

import org.example.bidverse_backend.autobid.handlers.AutoBidProcessor
import org.example.bidverse_backend.entities.AutoBid
import org.example.bidverse_backend.entities.Bid
import org.example.bidverse_backend.repositories.AuctionRepository
import org.example.bidverse_backend.repositories.AutoBidRepository
import org.example.bidverse_backend.repositories.BidRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Main service for executing autobids.
 * This will be called by the scheduler.
 */
@Service
class AutoBidExecutorService(
    private val autoBidRepository: AutoBidRepository,
    private val auctionRepository: AuctionRepository,
    private val bidRepository: BidRepository,
    private val autoBidProcessor: AutoBidProcessor
) {

    /**
     * Execute a single autobid
     * 
     * @param autoBidId The ID of the autobid to execute
     * @return The result of the execution
     */
    @Transactional
    fun executeAutoBid(autoBidId: Int): AutoBidExecutionResult {
        val autoBid = autoBidRepository.findById(autoBidId).orElse(null)
            ?: return AutoBidExecutionResult.NotFound("AutoBid not found")

        if (!autoBid.isActive) {
            return AutoBidExecutionResult.Skipped("AutoBid is not active")
        }

        val auction = autoBid.auction
        
        // Get all bids for this auction
        val allBids = bidRepository.findByAuctionIdOrderByValueDesc(auction.id!!)
        val currentHighestBid = allBids.firstOrNull()
        
        // Find the last bid placed by this autobid
        val lastBidByThisAutoBid = allBids.firstOrNull { it.bidder.id == autoBid.user.id }

        // Create context
        val context = AutoBidContext(
            autoBid = autoBid,
            auction = auction,
            user = autoBid.user,
            currentHighestBid = currentHighestBid,
            allBids = allBids,
            currentTime = LocalDateTime.now(),
            lastBidByThisAutoBid = lastBidByThisAutoBid
        )

        // Process the autobid
        val decision = autoBidProcessor.processAutoBid(context)

        // Update lastRun
        autoBid.lastRun = LocalDateTime.now()
        
        // Calculate next run if interval is set
        val intervalMinutes = autoBid.intervalMinutes
        if (intervalMinutes != null) {
            autoBid.nextRun = LocalDateTime.now().plusMinutes(intervalMinutes.toLong())
        }

        // Handle decision
        return when (decision) {
            is AutoBidDecision.PlaceBid -> {
                try {
                    // Place the bid
                    val bid = Bid(
                        id = null,
                        bidder = autoBid.user,
                        auction = auction,
                        value = decision.amount,
                        timeStamp = LocalDateTime.now(),
                        isWinning = true  // Will be updated by bid logic if needed
                    )
                    val savedBid = bidRepository.save(bid)
                    
                    // Update auction currentPrice
                    auction.lastBid = decision.amount
                    auctionRepository.save(auction)
                    
                    autoBidRepository.save(autoBid)
                    
                    AutoBidExecutionResult.BidPlaced(
                        amount = decision.amount,
                        reason = decision.reason,
                        bidId = savedBid.id!!
                    )
                } catch (e: Exception) {
                    AutoBidExecutionResult.Error("Failed to place bid: ${e.message}")
                }
            }
            
            is AutoBidDecision.SkipBid -> {
                autoBidRepository.save(autoBid)
                AutoBidExecutionResult.Skipped(decision.reason)
            }
            
            is AutoBidDecision.StopAutoBid -> {
                autoBid.isActive = false
                autoBidRepository.save(autoBid)
                AutoBidExecutionResult.Stopped(decision.reason)
            }
        }
    }

    /**
     * Execute all active autobids that are due
     */
    @Transactional
    fun executeAllDueAutoBids(): List<AutoBidExecutionSummary> {
        val now = LocalDateTime.now()
        val dueAutoBids = autoBidRepository.findAll()
            .filter { it.isActive && (it.nextRun == null || it.nextRun!!.isBefore(now)) }

        return dueAutoBids.map { autoBid ->
            val result = executeAutoBid(autoBid.id!!)
            AutoBidExecutionSummary(
                autoBidId = autoBid.id!!,
                auctionId = autoBid.auction.id!!,
                userId = autoBid.user.id!!,
                result = result
            )
        }
    }
}

/**
 * Result of executing a single autobid
 */
sealed class AutoBidExecutionResult {
    data class BidPlaced(val amount: java.math.BigDecimal, val reason: String, val bidId: Int) : AutoBidExecutionResult()
    data class Skipped(val reason: String) : AutoBidExecutionResult()
    data class Stopped(val reason: String) : AutoBidExecutionResult()
    data class NotFound(val reason: String) : AutoBidExecutionResult()
    data class Error(val reason: String) : AutoBidExecutionResult()
}

/**
 * Summary of autobid execution for reporting
 */
data class AutoBidExecutionSummary(
    val autoBidId: Int,
    val auctionId: Int,
    val userId: Int,
    val result: AutoBidExecutionResult
)
