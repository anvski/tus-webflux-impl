package github.anvski.reactivetusapi.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtServerSecurityContextRepository(
    private val authenticationManager: ReactiveAuthenticationManager
) : ServerSecurityContextRepository {

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> =
        Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { it.startsWith("Bearer ") }
            .flatMap {
                val bearerToken = it.substring(7)
                val authentication = UsernamePasswordAuthenticationToken(bearerToken, bearerToken)
                return@flatMap authenticationManager.authenticate(authentication)
            }.map { SecurityContextImpl(it) }



    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> = Mono.empty()

}