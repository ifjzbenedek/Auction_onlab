package org.example.bidverse_backend.Security;

import org.example.bidverse_backend.services.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
class SecurityConfig(private val customOAuth2UserService: CustomOAuth2UserService) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // PUBLIKUS ENDPOINT-OK
                    .requestMatchers(
                        "/",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/users/register",
                        "/users/login",
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/categories",
                        "/api/categories/**"
                    ).permitAll()

                    // AUKCIÓS ENDPOINT-OK - CSAK OLVASÁS
                    .requestMatchers("/auctions").permitAll()
                    .requestMatchers("/auctions/*/").permitAll()  // Aukció részletek
                    .requestMatchers("/auctions").permitAll()
                    .requestMatchers("/auctions/*/").permitAll()

                    // KÉPEK MEGTEKINTÉSE - PUBLIKUS
                    .requestMatchers("/auctions/*/images").permitAll()  // GET kérések a képekhez
                    .requestMatchers("/auctions/*/images").permitAll()

                    // MINDEN MÁS - AUTHENTIKÁCIÓ SZÜKSÉGES
                    // (Képfeltöltés POST, licitálás, stb.)
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
            .exceptionHandling { exceptions ->
                val ajaxEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                exceptions.defaultAuthenticationEntryPointFor(
                    ajaxEntryPoint,
                    RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest")
                )
            }
            .headers { headers ->
                headers
                    .xssProtection { it.disable() }
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