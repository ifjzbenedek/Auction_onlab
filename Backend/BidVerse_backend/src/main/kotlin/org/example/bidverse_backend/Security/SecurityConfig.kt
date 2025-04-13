package org.example.bidverse_backend.Security

import org.example.bidverse_backend.services.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(private val customOAuth2UserService: CustomOAuth2UserService) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) } // CORS beállítások
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // Nyilvános végpontok
                    .requestMatchers(
                        "/",
                        "/users/me",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/auctions/**",
                        "/auctions",
                        "/auctions/**",
                        "/users/register",
                        "/users/login",
                        "/oauth2/**",
                        "/categories",
                        "/api/auctions",          // Engedélyezd az összes aukció végpontot
                        "/api/auctions/**",
                        "/api/categories/**",
                        "/login/oauth2/**"
                    ).permitAll()
                    // Minden más végpont hitelesítést igényel
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .defaultSuccessUrl("http://localhost:5173/", true)
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }
            .headers { headers ->
                // Biztonsági fejlécek beállítása
                headers
                    .xssProtection { it.disable() }
                    .contentSecurityPolicy { policy ->
                        policy.policyDirectives("default-src 'self'")
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
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
            )
            exposedHeaders = listOf(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization",
                "Set-Cookie"
            )
            allowCredentials = true
            maxAge = 3600L
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}