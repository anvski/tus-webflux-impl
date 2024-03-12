package github.anvski.reactivetusapi.exceptions

class BadRequestException(private val customMessage: String = "Bad request") : RuntimeException(customMessage) {
}