package org.example.bidverse_backend.entities

import jakarta.persistence.*

@Entity
@Table(name = "Category")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId", nullable = false)
    val id: Int,

    @Column(nullable = false, length = 50)
    val categoryName: String,

    @OneToMany(mappedBy = "categoryId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val auctions: List<Auction>
)