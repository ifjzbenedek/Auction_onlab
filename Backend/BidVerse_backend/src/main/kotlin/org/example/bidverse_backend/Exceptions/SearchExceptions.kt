package org.example.bidverse_backend.Exceptions

class SearchServiceUnavailableException(message: String) : RuntimeException(message)
class InvalidSearchQueryException(message: String) : RuntimeException(message)
class SearchServiceTimeoutException(message: String) : RuntimeException(message)
class SearchIndexingException(message: String) : RuntimeException(message)