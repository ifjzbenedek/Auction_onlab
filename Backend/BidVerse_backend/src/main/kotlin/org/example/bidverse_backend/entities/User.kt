package org.example.bidverse_backend.entities

import jakarta.persistence.*

@Entity
@Table(name = "User", schema="dbo")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    var id: Int? = null,

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var auctions: MutableList<Auction> = mutableListOf(),

    @OneToMany(mappedBy = "bidder", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var bids: MutableList<Bid> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var watches: MutableList<Watch> = mutableListOf(),

    @Column(nullable = false, length = 25)
    var userName: String,

    @Column(nullable = false, length = 50)
    var emailAddress: String,

    @Column(nullable = false, length = 16)
    var phoneNumber: String,

    @Column(nullable = false, length = 20)
    var role: String = "USER"
)