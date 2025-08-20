package org.yosefdreams.diary.testconfig

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.yosefdreams.diary.security.JwtTokenProvider
import org.yosefdreams.diary.service.EmailService

@Configuration
class TestBeanConfig {

    @Bean
    @Primary
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Primary
    fun jwtTokenProvider(): JwtTokenProvider {
        return object : JwtTokenProvider("test-secret-key-1234567890-1234567890-1234567890", 86400000) {
            override fun generateToken(authentication: Authentication): String = "test-jwt-token"
        }
    }

    @Bean
    @Primary
    fun emailService(): EmailService = object : EmailService {
        override fun sendEmail(to: String, subject: String, text: String) {
            // No-op for tests
        }
    }

    @Bean
    @Primary
    fun authenticationManager(): AuthenticationManager = AuthenticationManager { authentication ->
        UsernamePasswordAuthenticationToken(authentication.principal, authentication.credentials)
    }

    @Bean
    @Primary
    fun javaMailSender(): JavaMailSender {
        return object : JavaMailSender {}
    }
}
