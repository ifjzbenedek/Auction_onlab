package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.entities.*
import org.example.bidverse_backend.services.*
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auctions")
class AuctionController(private val auctionService: AuctionService) {

}
