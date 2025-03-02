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
    var id: Int,

    @ManyToOne
    @JoinColumn(name= "ownerId", nullable = false)
    var userId: User,

    @ManyToOne
    @JoinColumn(name= "categoryId", nullable = false)
    var categoryId: Category,

    @OneToMany(mappedBy = "itemId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var bids: List<Bid>,

    @OneToMany(mappedBy = "itemId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var watchedAuctions: List<Watch>,

    @Column(nullable = false, length = 50)
    var itemName: String,

    @Column(nullable = false, precision = 12, scale = 2)
    var minimumPrice: BigDecimal,

    @Column(nullable = false, length = 20)
    var status: String,

    @Column(nullable = false)
    var createDate: LocalDateTime,

    @Column(nullable = false)
    var expiredDate: LocalDateTime,

    @Column(precision = 12, scale = 2)
    var lastBid: BigDecimal?,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false, length = 20)
    var type: String,

    var extraTime: LocalDateTime?,

    @Column(nullable = false, length = 20)
    var itemState: String,

    var tags: String,

    var minStep: Int?
)