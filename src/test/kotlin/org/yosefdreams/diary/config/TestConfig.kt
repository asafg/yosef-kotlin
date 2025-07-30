package org.yosefdreams.diary.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
@EnableWebSecurity
class TestConfig {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }
        return http.build()
    }
}
