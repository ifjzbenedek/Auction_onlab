package org.example.bidverse_backend.entities

import jakarta.persistence.*

@Entity
@Table(name = "Category")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId", nullable = false)
    var id: Int? = null,

    @Column(nullable = false, length = 50)
    var categoryName: String,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var auctions: MutableList<Auction> = mutableListOf()
)