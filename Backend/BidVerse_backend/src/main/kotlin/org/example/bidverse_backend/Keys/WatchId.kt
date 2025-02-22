package org.example.bidverse_backend.Keys

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class WatchId(
    val itemId: Int,
    val userId: Int
) : Serializable
