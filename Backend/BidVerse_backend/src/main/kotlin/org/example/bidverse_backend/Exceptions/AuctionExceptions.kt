package org.example.bidverse_backend.Exceptions

class AuctionNotFoundException(message: String) : RuntimeException(message)
class InvalidAuctionDataException(message: String) : RuntimeException(message)
class NotOnlyExtraDescriptionException(message: String): RuntimeException(message)
class DescriptionGenerationException(message: String) : RuntimeException(message)
class StatusNotFoundInRequestException(message: String) : RuntimeException(message)
