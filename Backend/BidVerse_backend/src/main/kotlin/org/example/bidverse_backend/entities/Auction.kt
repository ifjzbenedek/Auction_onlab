package org.example.bidverse_backend.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name="Auction", schema="dbo")
class Auction(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemId", nullable = false)
    val id: Int,

    @ManyToOne
    @JoinColumn(name= "ownerId", nullable = false)
    val userId: User,

    @ManyToOne
    @JoinColumn(name= "categoryId", nullable = false)
    val categoryId: Category,

    @OneToMany(mappedBy = "itemId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val bids: List<Bid>,

    @OneToMany(mappedBy = "itemId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val watchedAuctions: List<Watch>,

    @Column(nullable = false, length = 50)
    val itemName: String,

    @Column(nullable = false, precision = 12, scale = 2)
    val minimumPrice: BigDecimal,

    @Column(nullable = false, length = 20)
    val status: String,

    @Column(nullable = false)
    val createDate: LocalDateTime,

    @Column(nullable = false)
    val expiredDate: LocalDateTime,

    @Column(precision = 12, scale = 2)
    val lastBid: BigDecimal?,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false, length = 20)
    val type: String,

    val extraTime: LocalDateTime?,

    @Column(nullable = false, length = 20)
    val itemState: String,

    val tags: String,

    val minStep: Int?
)