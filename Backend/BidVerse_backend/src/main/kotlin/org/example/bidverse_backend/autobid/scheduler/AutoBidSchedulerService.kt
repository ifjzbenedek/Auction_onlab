package org.example.bidverse_backend.autobid.scheduler

import org.example.bidverse_backend.autobid.AutoBidExecutorService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * Scheduler service for executing autobids.
 * Runs every minute to check and execute due autobids.
 */
@Service
class AutoBidSchedulerService(
    private val autoBidExecutorService: AutoBidExecutorService
) {
    private val logger = LoggerFactory.getLogger(AutoBidSchedulerService::class.java)

    /**
     * Execute all due autobids every minute.
     * This checks for autobids where nextRun is null or in the past.
     */
    @Scheduled(fixedRate = 60000) // 60,000 ms = 1 minute
    fun executeScheduledAutoBids() {
        try {
            logger.info("Starting scheduled autobid execution...")
            val results = autoBidExecutorService.executeAllDueAutoBids()
            
            // Log summary
            val bidPlaced = results.count { it.result is org.example.bidverse_backend.autobid.AutoBidExecutionResult.BidPlaced }
            val skipped = results.count { it.result is org.example.bidverse_backend.autobid.AutoBidExecutionResult.Skipped }
            val stopped = results.count { it.result is org.example.bidverse_backend.autobid.AutoBidExecutionResult.Stopped }
            val errors = results.count { it.result is org.example.bidverse_backend.autobid.AutoBidExecutionResult.Error }
            
            logger.info(
                "Autobid execution completed. Total: ${results.size}, " +
                "Bids placed: $bidPlaced, Skipped: $skipped, Stopped: $stopped, Errors: $errors"
            )
            
            // Log details for bids that were placed
            results.filter { it.result is org.example.bidverse_backend.autobid.AutoBidExecutionResult.BidPlaced }
                .forEach { summary ->
                    val result = summary.result as org.example.bidverse_backend.autobid.AutoBidExecutionResult.BidPlaced
                    logger.info(
                        "Bid placed: AutoBid ${summary.autoBidId} on Auction ${summary.auctionId} " +
                        "for ${result.amount} - ${result.reason}"
                    )
                }
            
        } catch (e: Exception) {
            logger.error("Error during scheduled autobid execution", e)
        }  
    }
}
