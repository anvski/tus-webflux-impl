package github.anvski.reactivetusapi.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtReactiveAuthenticationManager(private val jwtTokenProvider: JwtTokenProvider) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val authenticationToken = authentication.credentials as String
        return Mono.just(jwtTokenProvider.validateToken(authenticationToken))
            .filter{ it }
            .switchIfEmpty(Mono.empty())
            .map { jwtTokenProvider.getAuthentication(authenticationToken) }
    }
}