package org.example.bidverse_backend.Exceptions

class AutoBidAlreadyExistsException(message: String) : RuntimeException(message)
class LLMServiceException(message: String) : RuntimeException(message)
class AutoBidNotFoundException(message: String) : RuntimeException(message)
