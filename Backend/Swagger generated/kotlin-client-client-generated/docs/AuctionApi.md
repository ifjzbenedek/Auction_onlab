# AuctionApi

All URIs are relative to *https://localhost:8081*

Method | HTTP request | Description
------------- | ------------- | -------------
[**auctionsAuctionIdBidsGet**](AuctionApi.md#auctionsAuctionIdBidsGet) | **GET** /auctions/{auctionId}/bids | Get all bids for a specific auction
[**auctionsAuctionIdBidsPost**](AuctionApi.md#auctionsAuctionIdBidsPost) | **POST** /auctions/{auctionId}/bids | Place a bid on an auction
[**auctionsAuctionIdDelete**](AuctionApi.md#auctionsAuctionIdDelete) | **DELETE** /auctions/{auctionId} | Delete auction by ID
[**auctionsAuctionIdGet**](AuctionApi.md#auctionsAuctionIdGet) | **GET** /auctions/{auctionId} | Get auction details by ID
[**auctionsAuctionIdPut**](AuctionApi.md#auctionsAuctionIdPut) | **PUT** /auctions/{auctionId} | Update auction details by ID
[**auctionsGet**](AuctionApi.md#auctionsGet) | **GET** /auctions | Get all auctions with optional filtering
[**auctionsMyBiddedAuctionsGet**](AuctionApi.md#auctionsMyBiddedAuctionsGet) | **GET** /auctions/my/biddedAuctions | Get auctions the user has bid on
[**auctionsMyCreatedAuctionsGet**](AuctionApi.md#auctionsMyCreatedAuctionsGet) | **GET** /auctions/my/createdAuctions | Get auctions created by the user
[**auctionsMyFollowedAuctionsGet**](AuctionApi.md#auctionsMyFollowedAuctionsGet) | **GET** /auctions/my/followedAuctions | Get auctions followed by the user
[**createAuction**](AuctionApi.md#createAuction) | **POST** /auctions | Create a new auction

<a name="auctionsAuctionIdBidsGet"></a>
# **auctionsAuctionIdBidsGet**
> kotlin.Array&lt;Bid&gt; auctionsAuctionIdBidsGet(auctionId)

Get all bids for a specific auction

Returns a list of all bids placed on a specific auction.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
val auctionId : kotlin.String = auctionId_example // kotlin.String | ID of the auction to retrieve bids for
try {
    val result : kotlin.Array<Bid> = apiInstance.auctionsAuctionIdBidsGet(auctionId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsAuctionIdBidsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsAuctionIdBidsGet")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **auctionId** | **kotlin.String**| ID of the auction to retrieve bids for |

### Return type

[**kotlin.Array&lt;Bid&gt;**](Bid.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="auctionsAuctionIdBidsPost"></a>
# **auctionsAuctionIdBidsPost**
> Bid auctionsAuctionIdBidsPost(auctionId, body)

Place a bid on an auction

Allows a user to place a bid on a specific auction.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
val auctionId : kotlin.String = auctionId_example // kotlin.String | ID of the auction to place the bid on
val body : Bid =  // Bid | Bid details
try {
    val result : Bid = apiInstance.auctionsAuctionIdBidsPost(auctionId, body)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsAuctionIdBidsPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsAuctionIdBidsPost")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **auctionId** | **kotlin.String**| ID of the auction to place the bid on |
 **body** | [**Bid**](Bid.md)| Bid details | [optional]

### Return type

[**Bid**](Bid.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="auctionsAuctionIdDelete"></a>
# **auctionsAuctionIdDelete**
> InlineResponse2001 auctionsAuctionIdDelete(auctionId)

Delete auction by ID

Delete a specific auction by its ID.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
val auctionId : kotlin.String = auctionId_example // kotlin.String | ID of the auction to update
try {
    val result : InlineResponse2001 = apiInstance.auctionsAuctionIdDelete(auctionId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsAuctionIdDelete")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsAuctionIdDelete")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **auctionId** | **kotlin.String**| ID of the auction to update |

### Return type

[**InlineResponse2001**](InlineResponse2001.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="auctionsAuctionIdGet"></a>
# **auctionsAuctionIdGet**
> AuctionBasic auctionsAuctionIdGet(auctionId)

Get auction details by ID

Returns basic information about a specific auction.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
val auctionId : kotlin.String = auctionId_example // kotlin.String | ID of the auction to retrieve
try {
    val result : AuctionBasic = apiInstance.auctionsAuctionIdGet(auctionId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsAuctionIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsAuctionIdGet")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **auctionId** | **kotlin.String**| ID of the auction to retrieve |

### Return type

[**AuctionBasic**](AuctionBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="auctionsAuctionIdPut"></a>
# **auctionsAuctionIdPut**
> AuctionBasic auctionsAuctionIdPut(body, auctionId)

Update auction details by ID

Update the details of a specific auction.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
val body : AuctionBasic =  // AuctionBasic | 
val auctionId : kotlin.String = auctionId_example // kotlin.String | ID of the auction to update
try {
    val result : AuctionBasic = apiInstance.auctionsAuctionIdPut(body, auctionId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsAuctionIdPut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsAuctionIdPut")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**AuctionBasic**](AuctionBasic.md)|  |
 **auctionId** | **kotlin.String**| ID of the auction to update |

### Return type

[**AuctionBasic**](AuctionBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="auctionsGet"></a>
# **auctionsGet**
> kotlin.Array&lt;AuctionCard&gt; auctionsGet(status, category)

Get all auctions with optional filtering

Returns all auctions in AuctionCard format, with optional filtering by status and category.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
val status : kotlin.String = status_example // kotlin.String | Filter auctions by status (e.g., ACTIVE, CLOSED)
val category : kotlin.String = category_example // kotlin.String | Filter auctions by category (e.g., Electronics, Vehicles, Furniture). Multiple categories can be provided, separated by commas.
try {
    val result : kotlin.Array<AuctionCard> = apiInstance.auctionsGet(status, category)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsGet")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **status** | **kotlin.String**| Filter auctions by status (e.g., ACTIVE, CLOSED) | [optional]
 **category** | **kotlin.String**| Filter auctions by category (e.g., Electronics, Vehicles, Furniture). Multiple categories can be provided, separated by commas. | [optional]

### Return type

[**kotlin.Array&lt;AuctionCard&gt;**](AuctionCard.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="auctionsMyBiddedAuctionsGet"></a>
# **auctionsMyBiddedAuctionsGet**
> kotlin.Array&lt;AuctionBasic&gt; auctionsMyBiddedAuctionsGet()

Get auctions the user has bid on

Returns all auctions the authenticated user has placed a bid on.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
try {
    val result : kotlin.Array<AuctionBasic> = apiInstance.auctionsMyBiddedAuctionsGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsMyBiddedAuctionsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsMyBiddedAuctionsGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.Array&lt;AuctionBasic&gt;**](AuctionBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="auctionsMyCreatedAuctionsGet"></a>
# **auctionsMyCreatedAuctionsGet**
> kotlin.Array&lt;AuctionBasic&gt; auctionsMyCreatedAuctionsGet()

Get auctions created by the user

Returns all auctions that the authenticated user has created.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
try {
    val result : kotlin.Array<AuctionBasic> = apiInstance.auctionsMyCreatedAuctionsGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsMyCreatedAuctionsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsMyCreatedAuctionsGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.Array&lt;AuctionBasic&gt;**](AuctionBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="auctionsMyFollowedAuctionsGet"></a>
# **auctionsMyFollowedAuctionsGet**
> kotlin.Array&lt;AuctionBasic&gt; auctionsMyFollowedAuctionsGet()

Get auctions followed by the user

Returns all auctions the authenticated user is following.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
try {
    val result : kotlin.Array<AuctionBasic> = apiInstance.auctionsMyFollowedAuctionsGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#auctionsMyFollowedAuctionsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#auctionsMyFollowedAuctionsGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.Array&lt;AuctionBasic&gt;**](AuctionBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="createAuction"></a>
# **createAuction**
> AuctionBasic createAuction(body)

Create a new auction

Creates a new auction for the authenticated user.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = AuctionApi()
val body : AuctionBasic =  // AuctionBasic | 
try {
    val result : AuctionBasic = apiInstance.createAuction(body)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuctionApi#createAuction")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuctionApi#createAuction")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**AuctionBasic**](AuctionBasic.md)|  |

### Return type

[**AuctionBasic**](AuctionBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

