package org.example.bidverse_backend.entities


import jakarta.persistence.*
import org.example.bidverse_backend.Utils.JsonConverter
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "AutoBid", schema="dbo")
class AutoBid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @ManyToOne
    @JoinColumn(name= "userId", nullable = false)
    var user: User,

    @ManyToOne
    @JoinColumn(name= "auctionId", nullable = false)
    var auction: Auction,

    @Column(nullable = true, precision = 18, scale = 2)
    var maxBidAmount: BigDecimal?,

    @Column(nullable = true, precision = 18, scale = 2)
    var incrementAmount: BigDecimal?,

    @Column(nullable = true)
    var intervalMinutes: Int?,

    @Column(nullable = true)
    var nextRun: LocalDateTime?,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Convert(converter = JsonConverter::class)
    @Column(nullable = true, columnDefinition = "NVARCHAR(MAX)")
    var conditionsJson: Map<String, Any>? = null,

    @Column(nullable = true)
    var lastRun: LocalDateTime?,

    @Column(nullable = false)
    var createdAt: LocalDateTime,

    @Column(nullable = true)
    var updatedAt: LocalDateTime?
)