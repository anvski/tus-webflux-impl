package github.anvski.reactivetusapi.controller.advice

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Mono


@ControllerAdvice
class AuthenticationExceptionHandler {

    @ExceptionHandler(value = [JwtException::class])
    fun invalidTokenExceptionValue(exception: JwtException): Mono<ResponseEntity<Any>> =
        Mono.just(ResponseEntity(HttpStatus.UNAUTHORIZED))


    @ExceptionHandler(value = [BadCredentialsException::class])
    fun badCredentialsException(exception: BadCredentialsException): Mono<ResponseEntity<Any>> =
        Mono.just(ResponseEntity("Refresh token haas expired or is wrong", HttpStatus.UNAUTHORIZED))

}