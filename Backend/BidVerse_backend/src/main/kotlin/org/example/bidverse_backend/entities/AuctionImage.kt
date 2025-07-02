package org.example.bidverse_backend.entities

import jakarta.persistence.*


@Entity
@Table(name = "AuctionImages", schema = "dbo")
class AuctionImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(name = "auctionId", nullable = false)
    val auction: Auction,

    @Column(nullable = false, length = 500)
    val cloudinaryUrl: String,

    @Column(nullable = false)
    val isPrimary: Boolean = false,

    @Column(nullable = false)
    val orderIndex: Int = 0,

    @ManyToOne
    @JoinColumn(name = "uploadedBy", nullable = false)
    val uploadedBy: User,

    @Column(nullable = false)
    val fileSizeKb: Int,

    @Column(nullable = false, length = 50)
    val format: String,

)