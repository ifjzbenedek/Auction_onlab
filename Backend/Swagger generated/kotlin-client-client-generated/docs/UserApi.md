# UserApi

All URIs are relative to *https://localhost:8081*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createUser**](UserApi.md#createUser) | **POST** /users/register | Create a new user
[**deleteUser**](UserApi.md#deleteUser) | **DELETE** /users/me | Delete a user
[**updateUserContact**](UserApi.md#updateUserContact) | **PUT** /users/me | Update a user&#x27;s email and phone number
[**usersLoginPost**](UserApi.md#usersLoginPost) | **POST** /users/login | User login
[**usersMeGet**](UserApi.md#usersMeGet) | **GET** /users/me | Get basic user profile (excluding bids, auctions, watches)

<a name="createUser"></a>
# **createUser**
> UserBasic createUser(body)

Create a new user

Registers a new user in the system.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = UserApi()
val body : UserCredentials =  // UserCredentials | 
try {
    val result : UserBasic = apiInstance.createUser(body)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling UserApi#createUser")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserApi#createUser")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**UserCredentials**](UserCredentials.md)|  |

### Return type

[**UserBasic**](UserBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="deleteUser"></a>
# **deleteUser**
> deleteUser()

Delete a user

Removes a user from the system.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = UserApi()
try {
    apiInstance.deleteUser()
} catch (e: ClientException) {
    println("4xx response calling UserApi#deleteUser")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserApi#deleteUser")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="updateUserContact"></a>
# **updateUserContact**
> UserBasic updateUserContact(body)

Update a user&#x27;s email and phone number

Allows updating only the email address and phone number of a user.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = UserApi()
val body : UserBasic =  // UserBasic | 
try {
    val result : UserBasic = apiInstance.updateUserContact(body)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling UserApi#updateUserContact")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserApi#updateUserContact")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**UserBasic**](UserBasic.md)|  |

### Return type

[**UserBasic**](UserBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="usersLoginPost"></a>
# **usersLoginPost**
> InlineResponse200 usersLoginPost(body)

User login

Validates a username and password.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = UserApi()
val body : UserCredentials =  // UserCredentials | 
try {
    val result : InlineResponse200 = apiInstance.usersLoginPost(body)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling UserApi#usersLoginPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserApi#usersLoginPost")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**UserCredentials**](UserCredentials.md)|  |

### Return type

[**InlineResponse200**](InlineResponse200.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="usersMeGet"></a>
# **usersMeGet**
> UserBasic usersMeGet()

Get basic user profile (excluding bids, auctions, watches)

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = UserApi()
try {
    val result : UserBasic = apiInstance.usersMeGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling UserApi#usersMeGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserApi#usersMeGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**UserBasic**](UserBasic.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

