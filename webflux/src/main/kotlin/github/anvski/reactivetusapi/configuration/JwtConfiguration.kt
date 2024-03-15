package github.anvski.reactivetusapi.configuration

import org.springframework.context.annotation.Configuration


@Configuration
class JwtConfiguration {
    private val authenticationSecretKey = "=====anvski============================"

    private val validityInMs: Long = 8640000000

    fun getAuthenticationSecretKey(): String = authenticationSecretKey

    fun getValidityInMs(): Long = validityInMs

}