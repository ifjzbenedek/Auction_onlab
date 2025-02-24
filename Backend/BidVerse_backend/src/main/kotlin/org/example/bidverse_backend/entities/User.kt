package org.example.bidverse_backend.entities

import jakarta.persistence.*

@Entity
@Table(name = "User", schema="dbo")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    val id: Int,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val auctions: List<Auction>,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val bids: List<Bid>,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val watches: List<Watch>,

    @Column(nullable = false, length = 25)
    val userName: String,

    @Column(nullable = false, length = 255)
    val passwordHash: String,

    @Column(nullable = false, length = 50)
    val emailAddress: String,

    @Column(nullable = false, length = 16)
    val phoneNumber: String
)