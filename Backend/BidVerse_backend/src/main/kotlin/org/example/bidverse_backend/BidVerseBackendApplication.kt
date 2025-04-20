package org.example.bidverse_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@SpringBootApplication
class BidVerseBackendApplication

fun main(args: Array<String>) {
	runApplication<BidVerseBackendApplication>(*args)
}
