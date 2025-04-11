package org.example.bidverse_backend.controllers

import org.example.bidverse_backend.Security.JwtTokenUtil
import org.example.bidverse_backend.Security.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val securityUtils: SecurityUtils
) {

    @GetMapping("/success")
    fun authSuccess(): ResponseEntity<Map<String, String>> {
        val userId = securityUtils.getCurrentUserId()
        val token = jwtTokenUtil.generateToken(userId.toString())
        return ResponseEntity.ok(mapOf("token" to token))
    }
}