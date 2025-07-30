package org.yosefdreams.diary.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.yosefdreams.diary.dto.LoginRequest
import org.yosefdreams.diary.dto.RegisterRequest
import org.yosefdreams.diary.entity.User
import org.yosefdreams.diary.repository.UserRepository

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )
        
        SecurityContextHolder.getContext().authentication = authentication
        
        return ResponseEntity.ok(mapOf("message" to "User logged in successfully"))
    }

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<Map<String, String>> {
        if (userRepository.existsByUsername(registerRequest.username)) {
            return ResponseEntity.badRequest().body(mapOf("message" to "Username is already taken!"))
        }

        if (userRepository.existsByEmail(registerRequest.email)) {
            return ResponseEntity.badRequest().body(mapOf("message" to "Email is already in use!"))
        }

        // Create new user's account
        val user = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password)
        )

        userRepository.save(user)

        return ResponseEntity.ok(mapOf("message" to "User registered successfully"))
    }
}
