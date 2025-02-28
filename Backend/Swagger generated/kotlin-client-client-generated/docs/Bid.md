# Bid

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | [**kotlin.Long**](.md) | The unique identifier of Bids |  [optional]
**itemId** | [**kotlin.Long**](.md) | The id of the Auctionitem |  [optional]
**userId** | [**kotlin.Long**](.md) | The id of the User, who made the Bid |  [optional]
**value** | [**kotlin.Double**](.md) | The money offer in dollars |  [optional]
**timeStamp** | [**java.time.LocalDateTime**](java.time.LocalDateTime.md) | The time, when the Bid was offered |  [optional]
**isWinning** | [**kotlin.Boolean**](.md) | True if this Bid is winning the Auction |  [optional]
