package org.example.bidverse_backend.DTOs.EntityToDTO

import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionBasicDTO
import org.example.bidverse_backend.DTOs.AuctionDTOs.AuctionCardDTO

import org.example.bidverse_backend.entities.Auction
import org.example.bidverse_backend.extensions.toUserBasicDTO
import org.example.bidverse_backend.services.AuctionUtils.AllAuctionUtils

fun Auction.toAuctionCardDTO(): AuctionCardDTO {
    return AuctionCardDTO(
        id = this.id!!,
        itemName = this.itemName,
        createDate = this.createDate,
        expiredDate = this.expiredDate,
        lastBid = this.lastBid,
        status = AllAuctionUtils.calculateStatus(this.startDate, this.expiredDate),
        startDate = this.startDate
    )
}

fun Auction.toAuctionBasicDTO(): AuctionBasicDTO {
    return AuctionBasicDTO(
        id = this.id!!,
        user = this.owner.toUserBasicDTO(),
        category = this.category.ToCategoryDTO(),
        itemName = this.itemName,
        minimumPrice = this.minimumPrice,
        status = AllAuctionUtils.calculateStatus(this.startDate, this.expiredDate),
        createDate = this.createDate,
        expiredDate = this.expiredDate,
        lastBid = this.lastBid,
        description = this.description,
        condition = this.condition,
        type = this.type,
        extraTime = this.extraTime,
        itemState = this.itemState,
        tags = this.tags,
        minStep = this.minStep,
        startDate = this.startDate
    )
}