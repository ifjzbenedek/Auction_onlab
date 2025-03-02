package org.example.bidverse_backend.entities

import jakarta.persistence.*

@Entity
@Table(name = "Category", schema="dbo")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId", nullable = false)
    var id: Int,

    @Column(nullable = false, length = 50)
    var categoryName: String,

    @OneToMany(mappedBy = "categoryId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var auctions: List<Auction>
)