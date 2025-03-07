package org.example.bidverse_backend.Exceptions

class UserNotFoundException(message: String) : RuntimeException(message)
class PermissionDeniedException(message: String) : RuntimeException(message)
class AuthenticationException(message: String) : RuntimeException(message)