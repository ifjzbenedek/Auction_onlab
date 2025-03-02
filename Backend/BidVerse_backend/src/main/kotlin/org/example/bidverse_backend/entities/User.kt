package org.example.bidverse_backend.entities

import jakarta.persistence.*

@Entity
@Table(name = "User", schema="dbo")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    var id: Int = -1,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var auctions: List<Auction>,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var bids: List<Bid>,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var watches: List<Watch>,

    @Column(nullable = false, length = 25)
    var userName: String,

    @Column(nullable = false, length = 255)
    var passwordHash: String,

    @Column(nullable = false, length = 50)
    var emailAddress: String,

    @Column(nullable = false, length = 16)
    var phoneNumber: String
)