package org.example.bidverse_backend

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Aspect
@Component
class RepositoryMonitoringAspect {

    private val logger = LoggerFactory.getLogger(RepositoryMonitoringAspect::class.java)

    // Timestamps of last calls by entity IDs
    private val lastSaveTimestamps = ConcurrentHashMap<String, Long>()

    // Time window for race condition detection (currently 100 ms, adjust as needed)
    private val raceConditionThresholdMs = 100

    @Around("execution(* org.example.bidverse_backend.repositories.AuctionRepository.save(..)) || " +
            "execution(* org.example.bidverse_backend.repositories.BidRepository.save(..))")
    fun logSaveInvocation(joinPoint: ProceedingJoinPoint): Any {
        val thread = Thread.currentThread().name
        val args = joinPoint.args

        val entity = args.firstOrNull()
        val entityId = try {
            val idProp = entity?.javaClass?.getMethod("getId")
            idProp?.invoke(entity)?.toString() ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }

        // Identify which repository is being called
        val repositoryName = when (joinPoint.signature.declaringTypeName) {
            "org.example.bidverse_backend.repositories.AuctionRepository" -> "Auction"
            "org.example.bidverse_backend.repositories.BidRepository" -> "Bid"
            else -> "Unknown"
        }

        val key = "$repositoryName-$entityId"

        val now = System.currentTimeMillis()
        val last = lastSaveTimestamps.put(key, now)

        if (last != null && now - last < raceConditionThresholdMs) {
            logger.warn("Potential race condition: ${repositoryName}Repository.save called twice within ${now - last}ms for id=$entityId")
        }

        logger.info("${repositoryName}Repository.save called from thread [$thread] for ${repositoryName} id=$entityId")

        return joinPoint.proceed()
    }
}
