package org.yosefdreams.diary.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@TestConfiguration
class TestSecurityConfig {
    
    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.builder()
            .username("testuser")
            .password("{noop}password")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
