package org.yosefdreams.diary.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.yosefdreams.diary.dto.ChangePasswordDto
import org.yosefdreams.diary.dto.JwtAuthResponse
import org.yosefdreams.diary.dto.LoginRequest
import org.yosefdreams.diary.dto.RegisterRequest
import org.yosefdreams.diary.entity.User
import org.yosefdreams.diary.repository.UserRepository
import org.yosefdreams.diary.security.JwtTokenProvider
import org.yosefdreams.diary.service.EmailService
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: JwtTokenProvider,
    private val emailService: EmailService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    companion object {
        const val RESET_TOKEN_MAX_AGE_MINUTES = 15L
    }

    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any>> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.usernameOrEmail, loginRequest.password)
        )
        
        SecurityContextHolder.getContext().authentication = authentication
        val token = tokenProvider.generateToken(authentication)
        
        return ResponseEntity.ok(mapOf(
            "accessToken" to token,
            "tokenType" to "Bearer"
        ))
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<Map<String, String>> {
        if (userRepository.existsByUsername(registerRequest.username)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Username is already taken!"))
        }

        if (userRepository.existsByEmail(registerRequest.email)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Email is already in use!"))
        }

        // Create new user's account
        val user = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password),
            name = registerRequest.name
        )

        userRepository.save(user)
        return ResponseEntity.ok(mapOf("message" to "User registered successfully"))
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestParam email: String): ResponseEntity<Map<String, String>> {
        val user = userRepository.findByEmail(email).orElse(null)
        
        if (user != null) {
            val resetToken = generateResetToken()
            
            user.resetToken = passwordEncoder.encode(resetToken)
            user.resetTokenCreationDate = LocalDateTime.now()
            userRepository.save(user)
            
            // Send email with reset token
            emailService.sendPasswordResetEmail(user.email, resetToken)
        }
        
        return ResponseEntity.ok(mapOf(
            "message" to "If your email is found in our database, a reset message will be sent to your email address"
        ))
    }
    
    @PostMapping("/change-password")
    fun changePassword(@Valid @RequestBody changePasswordDto: ChangePasswordDto): ResponseEntity<Map<String, String>> {
        val user = userRepository.findByEmail(changePasswordDto.email).orElse(null)
        
        if (user != null && user.resetToken != null) {
            val isTokenValid = passwordEncoder.matches(changePasswordDto.resetToken, user.resetToken)
            val hasTokenExpired = hasTokenExpired(user.resetTokenCreationDate)
            
            if (isTokenValid && !hasTokenExpired) {
                user.password = passwordEncoder.encode(changePasswordDto.newPassword)
                user.resetToken = null
                user.resetTokenCreationDate = null
                userRepository.save(user)
                return ResponseEntity.ok(mapOf("message" to "Password was changed successfully"))
            }
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Could not change password"))
    }
    
    private fun generateResetToken(): String {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString()
    }
    
    private fun hasTokenExpired(creationDate: LocalDateTime?): Boolean {
        if (creationDate == null) return true
        
        val now = LocalDateTime.now()
        val diff = Duration.between(creationDate, now)
        
        return diff.toMinutes() >= RESET_TOKEN_MAX_AGE_MINUTES
    }
}
