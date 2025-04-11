package org.example.bidverse_backend.Security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenUtil {

    @Value("\${jwt.secret}") // Titok a application.properties-ben
    private lateinit var secret: String

    @Value("\${jwt.expiration}") // Token érvényességi idő másodpercben
    private var expiration: Long = 86400 // Alapértelmezett 24 óra

    private val key: SecretKey
        get() = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(userId: String): String {
        val claims = mapOf("sub" to userId)
        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }
}
