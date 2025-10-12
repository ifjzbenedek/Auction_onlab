package org.example.bidverse_backend.Exceptions

class NotificationNotFoundException(message: String) : RuntimeException(message)
class NotificationPermissionDeniedException(message: String) : RuntimeException(message)
class NotificationAuctionNotFoundException(message: String) : RuntimeException(message)
