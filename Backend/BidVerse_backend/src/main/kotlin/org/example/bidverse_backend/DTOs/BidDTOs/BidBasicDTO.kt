package org.example.bidverse_backend.DTOs.BidDTOs

import org.example.bidverse_backend.DTOs.UserDTOs.UserCredentialsDTO
import java.math.BigDecimal
import java.time.LocalDateTime


class BidBasicDTO(
    val id: Int,
    val bidder: UserCredentialsDTO,
    val value: BigDecimal,
    val timeStamp: LocalDateTime,
    val isWinning: Boolean
)