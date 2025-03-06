package org.example.bidverse_backend.Security

import org.example.bidverse_backend.services.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(private val customOAuth2UserService: CustomOAuth2UserService) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/users/register").permitAll() // A regisztráció engedélyezett
                    .requestMatchers("/oauth2/**").permitAll() // Engedélyezzük az OAuth2 útvonalait
                    .anyRequest().authenticated() // Minden más endpointhoz hitelesítés kell
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfo ->
                        userInfo.userService(customOAuth2UserService) // Az egyéni OAuth2UserService használata
                    }
                    .defaultSuccessUrl("/users/me", true) // Sikeres bejelentkezés után ide irányít
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }

        return http.build()
    }
}

