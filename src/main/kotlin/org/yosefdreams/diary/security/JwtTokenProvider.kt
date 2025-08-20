package org.yosefdreams.diary.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    @Value("\${app.jwt.secret}")
    private val jwtSecret: String = ""

    @Value("\${app.jwt.expiration}")
    private val jwtExpirationInMs: Long = 604800000 // 7 days

    private val key: Key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)

        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getUsernameFromJWT(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }

    fun validateToken(authToken: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
            true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature")
            false
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
            false
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
            false
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
            false
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty")
            false
        }
    }
}
