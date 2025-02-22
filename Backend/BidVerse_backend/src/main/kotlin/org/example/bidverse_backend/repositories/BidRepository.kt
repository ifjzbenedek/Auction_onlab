package org.example.bidverse_backend.repositories

import org.example.bidverse_backend.entities.Bid
import org.springframework.data.jpa.repository.JpaRepository

interface BidRepository : JpaRepository<Bid, Long>