package github.anvski.reactivetusapi.security


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
class SecurityConfig(private val securityContextRepository: ServerSecurityContextRepository) {
    @Bean
    fun springWebFilterChain(
        http: ServerHttpSecurity,
        tokenProvider: JwtTokenProvider,
        reactiveAuthenticationManager: ReactiveAuthenticationManager
    ): SecurityWebFilterChain {
        return http.csrf().disable()
            .httpBasic().authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
            .authenticationManager(reactiveAuthenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange { it: AuthorizeExchangeSpec ->
                it
                    .pathMatchers(HttpMethod.POST, "/upload/**", "/upload").authenticated()
                    .pathMatchers(HttpMethod.OPTIONS, "/upload/**", "/upload").permitAll()
                    .pathMatchers(HttpMethod.PATCH, "/upload/**", "/upload").authenticated()
                    .pathMatchers(HttpMethod.HEAD, "/upload/**", "/upload").authenticated()
                    .pathMatchers(HttpMethod.DELETE, "/upload/**", "/upload").authenticated()
                    .anyExchange().permitAll()
            }.formLogin().disable()
            .build()
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val config = CorsConfiguration()
        config.addAllowedOrigin("http://localhost:4200")
        config.addAllowedMethod(HttpMethod.GET)
        config.addAllowedMethod(HttpMethod.POST)
        config.addAllowedMethod(HttpMethod.PUT)
        config.addAllowedMethod(HttpMethod.DELETE)
        config.addAllowedMethod(HttpMethod.OPTIONS)
        config.addAllowedMethod(HttpMethod.HEAD)
        config.addAllowedMethod(HttpMethod.PATCH)
        config.addAllowedHeader("Content-Type")
        config.addAllowedHeader("x-xsrf-token")
        config.addAllowedHeader("Authorization")
        config.addAllowedHeader("Access-Control-Allow-Headers")
        config.addAllowedHeader("Access-Control-Allow-Origin")
        config.addAllowedHeader("Origin")
        config.addAllowedHeader("Accept")
        config.addAllowedHeader("X-Requested-With")
        config.addAllowedHeader("Access-Control-Request-Method")
        config.addAllowedHeader("Access-Control-Request-Headers")
        config.addAllowedHeader("Mime-Type")
        config.addAllowedHeader("Tus-Resumable")
        config.addAllowedHeader("Upload-Length")
        config.addAllowedHeader("Upload-Metadata")
        config.addAllowedHeader("Upload-Offset")
        config.addAllowedHeader("Location")
        //Sec-Fetch-Mode
        config.addAllowedHeader("Sec-Fetch-Dest")
        config.addAllowedHeader("Sec-Fetch-Mode")
        config.addAllowedHeader("Sec-Fetch-Site")

        config.exposedHeaders = config.allowedHeaders

        config.maxAge = 3600L
        val corsConfigurationSource = UrlBasedCorsConfigurationSource()
        corsConfigurationSource.registerCorsConfiguration("/upload", config)
        corsConfigurationSource.registerCorsConfiguration("/upload/**", config)
        corsConfigurationSource.registerCorsConfiguration("/auth/**", config)
        corsConfigurationSource.registerCorsConfiguration("/auth", config)
         return CorsWebFilter(
             corsConfigurationSource
         )
    }
}