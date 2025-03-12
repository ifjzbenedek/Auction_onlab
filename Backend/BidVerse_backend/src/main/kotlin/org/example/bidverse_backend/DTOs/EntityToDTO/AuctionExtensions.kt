package org.example.bidverse_backend.DTOs.EntityToDTO

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO
import org.example.bidverse_backend.entities.Auction

fun Auction.toAuctionCardDTO(): AuctionCardDTO {
    return AuctionCardDTO(
        id = this.id!!,
        itemName = this.itemName,
        createDate = this.createDate,
        expiredDate = this.expiredDate,
        lastBid = this.lastBid
    )
}

fun Auction.toAuctionBasicDTO(): AuctionBasicDTO {
    return AuctionBasicDTO(
        id = this.id!!,
        user = this.owner,
        category = this.category,
        itemName = this.itemName,
        minimumPrice = this.minimumPrice,
        status = this.status,
        createDate = this.createDate,
        expiredDate = this.expiredDate,
        lastBid = this.lastBid,
        description = this.description,
        type = this.type,
        extraTime = this.extraTime,
        itemState = this.itemState,
        tags = this.tags,
        minStep = this.minStep
    )
}