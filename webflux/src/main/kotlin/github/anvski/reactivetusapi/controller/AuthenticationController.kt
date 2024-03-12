package github.anvski.reactivetusapi.controller

import github.anvski.reactivetusapi.controller.dto.LoginRequest
import github.anvski.reactivetusapi.controller.dto.RegisterRequest
import github.anvski.reactivetusapi.security.JwtTokenProvider
import github.anvski.reactivetusapi.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: JwtTokenProvider
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): Mono<ResponseEntity<Map<String, String>>> {
        return userService.findByUsername(loginRequest.username)
            .switchIfEmpty(Mono.error(RuntimeException("Username or password is incorrect")))
            .flatMap { user ->
                (passwordEncoder.matches(loginRequest.password, user.password)).takeIf { it }?.let {
                    Mono.just(
                        tokenProvider.createToken(
                            UsernamePasswordAuthenticationToken(
                                user.username,
                                user.username
                            )
                        )
                    )
                } ?: Mono.error(RuntimeException("Username or password is incorrect"))
            }.map {
                ResponseEntity(mapOf("access_token" to it), null, HttpStatus.OK)
            }
    }

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): Mono<ResponseEntity<Any>> {
        return checkRegisterRequestValidity(registerRequest).filter { it }
            .flatMap {
                userService.save(registerRequest.username, registerRequest.email, registerRequest.password)
            }.map { ResponseEntity.ok().build<Any>() }
            .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
    }

    private fun checkRegisterRequestValidity(registerRequest: RegisterRequest) : Mono<Boolean> =
        Mono.just(registerRequest.username.length > 5 && registerRequest.password.length > 5)

}