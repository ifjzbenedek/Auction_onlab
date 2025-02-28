# io.swagger.client - Kotlin client library for BidVerse

## Requires

* Kotlin 1.4.30
* Gradle 5.3

## Build

First, create the gradle wrapper script:

```
gradle wrapper
```

Then, run:

```
./gradlew check assemble
```

This runs all tests and packages the library.

## Features/Implementation Notes

* Supports JSON inputs/outputs, File inputs, and Form inputs.
* Supports collection formats for query parameters: csv, tsv, ssv, pipes.
* Some Kotlin and Java types are fully qualified to avoid conflicts with types defined in Swagger definitions.
* Implementation of ApiClient is intended to reduce method counts, specifically to benefit Android targets.

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://localhost:8081*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AuctionApi* | [**auctionsAuctionIdBidsGet**](docs/AuctionApi.md#auctionsauctionidbidsget) | **GET** /auctions/{auctionId}/bids | Get all bids for a specific auction
*AuctionApi* | [**auctionsAuctionIdBidsPost**](docs/AuctionApi.md#auctionsauctionidbidspost) | **POST** /auctions/{auctionId}/bids | Place a bid on an auction
*AuctionApi* | [**auctionsAuctionIdDelete**](docs/AuctionApi.md#auctionsauctioniddelete) | **DELETE** /auctions/{auctionId} | Delete auction by ID
*AuctionApi* | [**auctionsAuctionIdGet**](docs/AuctionApi.md#auctionsauctionidget) | **GET** /auctions/{auctionId} | Get auction details by ID
*AuctionApi* | [**auctionsAuctionIdPut**](docs/AuctionApi.md#auctionsauctionidput) | **PUT** /auctions/{auctionId} | Update auction details by ID
*AuctionApi* | [**auctionsGet**](docs/AuctionApi.md#auctionsget) | **GET** /auctions | Get all auctions with optional filtering
*AuctionApi* | [**auctionsMyBiddedAuctionsGet**](docs/AuctionApi.md#auctionsmybiddedauctionsget) | **GET** /auctions/my/biddedAuctions | Get auctions the user has bid on
*AuctionApi* | [**auctionsMyCreatedAuctionsGet**](docs/AuctionApi.md#auctionsmycreatedauctionsget) | **GET** /auctions/my/createdAuctions | Get auctions created by the user
*AuctionApi* | [**auctionsMyFollowedAuctionsGet**](docs/AuctionApi.md#auctionsmyfollowedauctionsget) | **GET** /auctions/my/followedAuctions | Get auctions followed by the user
*AuctionApi* | [**createAuction**](docs/AuctionApi.md#createauction) | **POST** /auctions | Create a new auction
*CategoryApi* | [**categoriesGet**](docs/CategoryApi.md#categoriesget) | **GET** /categories | Get all categories
*UserApi* | [**createUser**](docs/UserApi.md#createuser) | **POST** /users/register | Create a new user
*UserApi* | [**deleteUser**](docs/UserApi.md#deleteuser) | **DELETE** /users/me | Delete a user
*UserApi* | [**updateUserContact**](docs/UserApi.md#updateusercontact) | **PUT** /users/me | Update a user's email and phone number
*UserApi* | [**usersLoginPost**](docs/UserApi.md#usersloginpost) | **POST** /users/login | User login
*UserApi* | [**usersMeGet**](docs/UserApi.md#usersmeget) | **GET** /users/me | Get basic user profile (excluding bids, auctions, watches)

<a name="documentation-for-models"></a>
## Documentation for Models

 - [io.swagger.client.models.Auction](docs/Auction.md)
 - [io.swagger.client.models.AuctionBasic](docs/AuctionBasic.md)
 - [io.swagger.client.models.AuctionCard](docs/AuctionCard.md)
 - [io.swagger.client.models.Bid](docs/Bid.md)
 - [io.swagger.client.models.Category](docs/Category.md)
 - [io.swagger.client.models.InlineResponse200](docs/InlineResponse200.md)
 - [io.swagger.client.models.InlineResponse2001](docs/InlineResponse2001.md)
 - [io.swagger.client.models.InlineResponse401](docs/InlineResponse401.md)
 - [io.swagger.client.models.User](docs/User.md)
 - [io.swagger.client.models.UserBasic](docs/UserBasic.md)
 - [io.swagger.client.models.UserCredentials](docs/UserCredentials.md)
 - [io.swagger.client.models.Watch](docs/Watch.md)
 - [io.swagger.client.models.WatchId](docs/WatchId.md)

<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
