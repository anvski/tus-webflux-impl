package github.anvski.reactivetusapi.exceptions

class CommonException(private val customMessage: String = "Internal server error") : RuntimeException(customMessage) {
}