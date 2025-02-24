package org.example.bidverse_backend.entities

import jakarta.persistence.*
import org.example.bidverse_backend.Keys.WatchId

@Entity
@Table(name = "Watch", schema="dbo")
class Watch(

    @EmbeddedId
    val id: WatchId,

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name= "auctionId", nullable = false)
    val itemId: Auction,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name= "userId", nullable = false)
    val userId: User
)

