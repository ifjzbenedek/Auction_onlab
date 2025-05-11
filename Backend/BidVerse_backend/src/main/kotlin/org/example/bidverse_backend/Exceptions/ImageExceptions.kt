package org.example.bidverse_backend.Exceptions

class ImageUploadException(message: String) : RuntimeException(message)
class ImageDeleteException(message: String) : RuntimeException(message)
class ImageNotFoundException(message: String) : RuntimeException(message)
class ImageValidationException(message: String) : RuntimeException(message)
class ImageCountLowException(message: String) : RuntimeException(message)
class ImageCountHighException(message: String) : RuntimeException(message)
class PrimaryImageException(message: String) : RuntimeException(message)

class ImageProcessingException(message: String) : RuntimeException(message)