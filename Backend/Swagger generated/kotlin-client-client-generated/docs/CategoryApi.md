# CategoryApi

All URIs are relative to *https://localhost:8081*

Method | HTTP request | Description
------------- | ------------- | -------------
[**categoriesGet**](CategoryApi.md#categoriesGet) | **GET** /categories | Get all categories

<a name="categoriesGet"></a>
# **categoriesGet**
> kotlin.Array&lt;Category&gt; categoriesGet()

Get all categories

Returns a list of all available categories.

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*;

val apiInstance = CategoryApi()
try {
    val result : kotlin.Array<Category> = apiInstance.categoriesGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CategoryApi#categoriesGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CategoryApi#categoriesGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.Array&lt;Category&gt;**](Category.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

