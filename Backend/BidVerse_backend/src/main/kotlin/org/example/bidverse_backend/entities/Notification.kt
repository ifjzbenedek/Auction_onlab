package org.example.bidverse_backend.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "Notification")
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @ManyToOne
    @JoinColumn(name= "senderId", nullable = true)
    var sender: User? = null,

    @ManyToOne
    @JoinColumn(name= "receiverId", nullable = false)
    var receiver: User,

    @ManyToOne
    @JoinColumn(name= "auctionId", nullable = true)
    var auction: Auction? = null,

    @Column(nullable = false)
    var createdAt: LocalDateTime,

    @Column(nullable = false, length = 499)
    var messageText: String,

    @Column(nullable = true, length = 99)
    var titleText: String?,

    @Column(nullable = false)
    var alreadyOpened: Boolean = false
)
