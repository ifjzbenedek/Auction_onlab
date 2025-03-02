package org.example.bidverse_backend.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "Bid", schema="dbo")
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bidId", nullable = false)
    var id: Int,

    @ManyToOne
    @JoinColumn(name= "auctionId", nullable = false)
    var itemId: Auction,

    @ManyToOne
    @JoinColumn(name= "userId", nullable = false)
    var userId: User,

    @Column(name = "value", nullable = false, precision = 12, scale = 2)
    var value: BigDecimal,

    @Column(name = "timeStamp", nullable = false)
    var timeStamp: LocalDateTime,

    @Column(name = "isWinning", nullable = false)
    var isWinning: Boolean
)
