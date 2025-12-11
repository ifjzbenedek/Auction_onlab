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
    var id: Int? = null,

    @ManyToOne
    @JoinColumn(name= "auctionId", nullable = false)
    var auction: Auction,

    @ManyToOne
    @JoinColumn(name= "userId", nullable = false)
    var bidder: User,

    @Column(name = "value", nullable = false, precision = 12, scale = 2)
    var value: BigDecimal,

    @Column(name = "timeStamp", nullable = false)
    var timeStamp: LocalDateTime,

    @Column(name = "isWinning", nullable = false)
    var isWinning: Boolean,

    @Version
    val version: Long = 0
)
