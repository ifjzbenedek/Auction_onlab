package org.example.bidverse_backend.DTOs.BidDTOs

import java.math.BigDecimal

data class BidRequestDTO(
    val amount: BigDecimal
)