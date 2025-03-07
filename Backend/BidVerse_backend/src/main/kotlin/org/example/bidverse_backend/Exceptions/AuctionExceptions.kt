package org.example.bidverse_backend.Exceptions

class AuctionNotFoundException(message: String) : RuntimeException(message)
class InvalidAuctionDataException(message: String) : RuntimeException(message)