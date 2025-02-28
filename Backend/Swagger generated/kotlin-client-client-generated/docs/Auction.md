# Auction

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | [**kotlin.Long**](.md) | The unique identifier of Auctions |  [optional]
**userId** | [**kotlin.Long**](.md) | The user who created the Auction |  [optional]
**categoryId** | [**kotlin.Long**](.md) | The category of the Auction |  [optional]
**bids** | [**kotlin.Array&lt;Bid&gt;**](Bid.md) | The offered Bids for the Auction |  [optional]
**watchedAuctions** | [**kotlin.Array&lt;Watch&gt;**](Watch.md) | List of users watching this Auction |  [optional]
**itemName** | [**kotlin.String**](.md) | Name of the Auction item |  [optional]
**minimumPrice** | [**kotlin.Double**](.md) | Minimum selling price of the item |  [optional]
**status** | [**kotlin.String**](.md) | Current status of the Auction (e.g., active, closed) |  [optional]
**createDate** | [**java.time.LocalDateTime**](java.time.LocalDateTime.md) | Date and time when the Auction was created |  [optional]
**expiredDate** | [**java.time.LocalDateTime**](java.time.LocalDateTime.md) | Date and time when the Auction expires |  [optional]
**lastBid** | [**kotlin.Double**](.md) | Latest Bid amount, if any |  [optional]
**description** | [**kotlin.String**](.md) | Detailed description of the item |  [optional]
**type** | [**kotlin.String**](.md) | Type of the Auction (e.g., timed, restarting) |  [optional]
**extraTime** | [**java.time.LocalDateTime**](java.time.LocalDateTime.md) | Extra time added to the auction if it&#x27;s a restarting one |  [optional]
**itemState** | [**kotlin.String**](.md) | State of the item (e.g., new, used) |  [optional]
**tags** | [**kotlin.String**](.md) | Keywords associated with the Auction item |  [optional]
**minStep** | [**kotlin.Int**](.md) | Minimum increment step for Bids |  [optional]
