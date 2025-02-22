package org.example.bidverse_backend.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "Bid")
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bidId", nullable = false)
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(name= "auctionId", nullable = false)
    val itemId: Auction,

    @ManyToOne
    @JoinColumn(name= "userId", nullable = false)
    val userId: User,

    @Column(name = "value", nullable = false, precision = 12, scale = 2)
    val value: BigDecimal,

    @Column(name = "timeStamp", nullable = false)
    val timeStamp: LocalDateTime,

    @Column(name = "isWinning", nullable = false)
    val isWinning: Boolean
)
