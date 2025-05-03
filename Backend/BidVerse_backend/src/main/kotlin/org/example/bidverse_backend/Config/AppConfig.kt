package org.example.bidverse_backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory().apply {
            setConnectTimeout(10000)   //10mp
            setReadTimeout(60000)     //1p
        }
        return RestTemplate(factory)
    }
}

