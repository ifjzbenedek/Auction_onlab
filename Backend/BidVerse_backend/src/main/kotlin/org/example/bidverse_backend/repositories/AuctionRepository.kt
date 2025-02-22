package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.Auction
import org.springframework.data.jpa.repository.JpaRepository

interface AuctionRepository : JpaRepository<Auction, Long>