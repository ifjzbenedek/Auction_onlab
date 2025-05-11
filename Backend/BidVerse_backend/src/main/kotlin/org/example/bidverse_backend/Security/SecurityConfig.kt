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
            // Consider enabling CSRF with CookieCsrfTokenRepository if your SPA can handle it
            // For now, keeping it disabled as per your original config
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/",
                        "/users/me", // This might need authentication depending on its purpose
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api/auctions/**", // Covered by /auctions/** if Vite rewrites /api -> /
                        "/auctions",
                        "/auctions/**",     // This will match requests like /auctions/ID/images after Vite proxy rewrite
                        "/users/register",
                        "/users/login",     // This is where your frontend redirects for login, which then triggers OAuth
                        "/oauth2/**",       // Backend endpoints for OAuth flow
                        "/login/oauth2/**", // Backend callback endpoint
                        "/categories",
                        "/api/categories/**" // Covered by /categories/** if Vite rewrites /api -> /
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .defaultSuccessUrl("http://localhost:5173/", true)
                // Optionally, you can specify the login page/initiation URL
                // .loginPage("/oauth2/authorization/google") // Or your custom frontend login page
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Or SessionCreationPolicy.STATELESS if you move to tokens fully
            }
            .exceptionHandling { exceptions ->
                // For AJAX requests (typically those expecting JSON or with X-Requested-With), return 401
                // instead of redirecting to login page.
                val ajaxEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                exceptions.defaultAuthenticationEntryPointFor(
                    ajaxEntryPoint,
                    RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest")
                )
                // You might want another entry point for non-ajax requests if you don't want the default oauth2Login behavior for all
            }
            .headers { headers ->
                headers
                    .xssProtection { it.disable() } // Generally recommended to rely on modern browser XSS protection
                    .contentSecurityPolicy { policy ->
                        policy.policyDirectives(
                            "default-src 'self';" +
                                    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://accounts.google.com;" + // 'unsafe-inline/eval' for dev, try to remove for prod
                                    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com;" +
                                    "img-src 'self' data: https:;" + // Allow images from self, data URLs, and any HTTPS source
                                    "font-src 'self' https://fonts.gstatic.com;" +
                                    "connect-src 'self' http://localhost:5173 https://localhost:5173 https://localhost:8081 wss://localhost:8081 https://accounts.google.com;" + // Allow connections to self, frontend, backend, and Google
                                    "frame-src 'self' https://accounts.google.com;" + // Allow framing Google for OAuth iframes
                                    "object-src 'none';" +
                                    "base-uri 'self';" +
                                    "form-action 'self' https://accounts.google.com;" // Allow form submissions to Google
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
                "https://localhost:5173", // If your frontend ever runs on HTTPS
                "http://127.0.0.1:5173"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            allowedHeaders = listOf(
                "Authorization", "Cache-Control", "Content-Type", "X-Requested-With",
                "Accept", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers", "X-CSRF-TOKEN" // If you enable CSRF
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