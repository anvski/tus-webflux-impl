package github.anvski.reactivetusapi.security

import github.anvski.reactivetusapi.configuration.JwtConfiguration
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors.joining
import javax.crypto.SecretKey


@Component
class JwtTokenProvider(private val jwtConfigurationProperties: JwtConfiguration) {

    private var authenticationSecretKey: SecretKey? = null

    @PostConstruct
    protected fun init() {
        val secret: String = Base64.getEncoder().encodeToString(jwtConfigurationProperties.getAuthenticationSecretKey().toByteArray())
        authenticationSecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun createToken(authentication: Authentication): String {
        val username: String = authentication.name
        val authorities: Collection<GrantedAuthority> = authentication.authorities
        val claims = Jwts.claims().setSubject(username)
        claims[AUTHORITIES_KEY] =
            authorities.stream().map { obj: GrantedAuthority -> obj.authority }.collect(joining(","))
        val now = Date()
        val validity = Date(now.time + jwtConfigurationProperties.getValidityInMs())
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(authenticationSecretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String?): Authentication {
        val claims = Jwts.parserBuilder().setSigningKey(authenticationSecretKey).build()
            .parseClaimsJws(token)
            .body
        val authorities: Collection<GrantedAuthority> =
            AuthorityUtils.commaSeparatedStringToAuthorityList(claims[AUTHORITIES_KEY].toString())
        val principal = claims.subject
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(token: String?): Boolean {
        try {
            val claims = Jwts.parserBuilder().setSigningKey(authenticationSecretKey).build()
                .parseClaimsJws(token)
            return !claims.body.expiration.before(Date())
        } catch (_: Exception) {
        }
        return false
    }

    companion object {
        private const val AUTHORITIES_KEY = "roles"
    }
}