package org.example.bidverse_backend.Security

import org.example.bidverse_backend.services.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod

@Configuration
@EnableWebSecurity
class SecurityConfig(private val customOAuth2UserService: CustomOAuth2UserService) {

    companion object {
        private const val X_REQUESTED_WITH_HEADER = "X-Requested-With"
        private const val XML_HTTP_REQUEST = "XMLHttpRequest"
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints - no authentication required
                    .requestMatchers(HttpMethod.GET, "/auctions").permitAll()                    // List auctions
                    .requestMatchers(HttpMethod.GET, "/auctions/{id}").permitAll()              // Get specific auction
                    .requestMatchers(HttpMethod.GET, "/auctions/{id}/bids").permitAll()         // Get auction bids (public)
                    .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()              // Categories
                    .requestMatchers("/oauth2/**").permitAll()                                  // OAuth2 login
                    .requestMatchers("/login/**").permitAll()                                   // Login pages
                    .requestMatchers("/logout").permitAll()                                     // Logout endpoint
                    
                    // Protected auction endpoints - authentication required
                    .requestMatchers(HttpMethod.POST, "/auctions").authenticated()              // Create auction
                    .requestMatchers(HttpMethod.GET, "/auctions/smart-search").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/auctions/{id}").authenticated()          // Update auction
                    .requestMatchers(HttpMethod.DELETE, "/auctions/{id}").authenticated()       // Delete auction
                    .requestMatchers(HttpMethod.GET, "/auctions/my/**").authenticated()         // My auctions/watched/bidded
                    .requestMatchers(HttpMethod.POST, "/auctions/{id}/bids").authenticated()    // Place bid
                    .requestMatchers("/auctions/{id}/watch").authenticated()                    // Watch/unwatch auction
                    
                    // User endpoints - authentication required
                    .requestMatchers(HttpMethod.GET, "/users/me").authenticated()               // Current user info
                    .requestMatchers(HttpMethod.GET, "/users/{id}").authenticated()            // User profile
                    
                    // Other bid endpoints (besides GET auction bids)
                    .requestMatchers("/bids/**").authenticated()
                    
                    // All other requests
                    .anyRequest().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfo ->
                        userInfo.oidcUserService(customOAuth2UserService)
                    }
                    .defaultSuccessUrl("http://localhost:5173/", true)
                    .failureUrl("http://localhost:5173/login?error=true")
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("http://localhost:5173/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID", "SESSION")
                    .permitAll()
                    // Handle OAuth2 logout properly
                    .addLogoutHandler { request, response, authentication ->
                        // Force clear security context
                        org.springframework.security.core.context.SecurityContextHolder.clearContext()
                        
                        // Clear all cookies manually
                        request.cookies?.forEach { cookie ->
                            val newCookie = jakarta.servlet.http.Cookie(cookie.name, null)
                            newCookie.maxAge = 0
                            newCookie.path = "/"
                            response.addCookie(newCookie)
                        }
                        
                        println("Custom logout handler executed for user: ${authentication?.name}")
                    }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }
            .exceptionHandling { exceptions ->
                // For AJAX requests, return 401 status
                val ajaxEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                exceptions.defaultAuthenticationEntryPointFor(
                    ajaxEntryPoint,
                    RequestHeaderRequestMatcher(X_REQUESTED_WITH_HEADER, XML_HTTP_REQUEST)
                )

                // For regular browser requests, redirect to Google OAuth login
                exceptions.authenticationEntryPoint { request, response, _ ->
                    // Check if it's a browser request (not AJAX)
                    val isAjax = XML_HTTP_REQUEST == request.getHeader(X_REQUESTED_WITH_HEADER)
                    val acceptsJson = request.getHeader("Accept")?.contains("application/json") == true

                    if (isAjax || acceptsJson) {
                        // For AJAX/API requests, return JSON with auth URL
                        response.contentType = "application/json"
                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.writer.write("""
                            {
                                "error": "Authentication required",
                                "message": "Please authenticate to access this resource",
                                "authUrl": "/oauth2/authorization/google"
                            }
                        """.trimIndent())
                    } else {
                        // For browser requests, redirect to Google OAuth
                        response.sendRedirect("/oauth2/authorization/google")
                    }
                }
            }
            .headers { headers ->
                headers
                    .contentSecurityPolicy { policy ->
                        policy.policyDirectives(
                            "default-src 'self';" +
                                    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://accounts.google.com;" +
                                    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com;" +
                                    "img-src 'self' data: https:;" +
                                    "font-src 'self' https://fonts.gstatic.com;" +
                                    "connect-src 'self' http://localhost:5173 https://localhost:5173 https://localhost:8081 wss://localhost:8081 https://accounts.google.com;" +
                                    "frame-src 'self' https://accounts.google.com;" +
                                    "object-src 'none';" +
                                    "base-uri 'self';" +
                                    "form-action 'self' https://accounts.google.com;"
                        )
                    }
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://localhost:5173",
                "https://localhost:5173",
                "http://127.0.0.1:5173"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            allowedHeaders = listOf(
                "Authorization", "Cache-Control", "Content-Type", "X-Requested-With",
                "Accept", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers", "X-CSRF-TOKEN"
            )
            exposedHeaders = listOf(
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
                "Authorization", "Set-Cookie"
            )
            allowCredentials = true
            maxAge = 3600L
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}