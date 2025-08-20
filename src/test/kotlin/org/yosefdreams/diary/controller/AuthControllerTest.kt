package org.yosefdreams.diary.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.yosefdreams.diary.config.TestSecurityConfig
import org.yosefdreams.diary.dto.LoginRequest
import org.yosefdreams.diary.dto.RegisterRequest
import org.yosefdreams.diary.entity.User
import org.yosefdreams.diary.repository.UserRepository
import org.yosefdreams.diary.security.JwtTokenProvider
import org.yosefdreams.diary.service.EmailService
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(AuthController::class)
@Import(TestSecurityConfig::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    private lateinit var passwordEncoder: PasswordEncoder

    @MockBean
    private lateinit var authenticationManager: AuthenticationManager

    @MockBean
    private lateinit var emailService: EmailService

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    
    @BeforeEach
    fun setup() {
        // Setup common mock behaviors
        every { passwordEncoder.encode(any<String>()) } returns "hashed_password"
        every { passwordEncoder.matches(any(), any()) } returns true
    }

    @Test
    fun `register user with valid data should return ok`() {
        // Given
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password",
            name = "Test User"
        )

        val user = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = "hashed_password",
            name = registerRequest.name
        )

        every { userRepository.existsByUsername(any()) } returns false
        every { userRepository.existsByEmail(any()) } returns false
        every { userRepository.save(any()) } returns user
        every { passwordEncoder.encode(any()) } returns "hashed_password"

        // When/Then
        mockMvc.post("/api/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.message") { value("User registered successfully") }
        }
    }

    @Test
    fun `register with existing username should return bad request`() {
        // Given
        val registerRequest = RegisterRequest(
            username = "existinguser",
            email = "test@example.com",
            password = "password",
            name = "Test User"
        )

        every { userRepository.existsByUsername(registerRequest.username) } returns true

        // When/Then
        mockMvc.post("/api/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("Username is already taken!") }
        }
    }

    @Test
    fun `login with valid credentials should return token`() {
        // Given
        val loginRequest = LoginRequest(
            usernameOrEmail = "testuser",
            password = "password"
        )

        val auth = UsernamePasswordAuthenticationToken(loginRequest.usernameOrEmail, loginRequest.password)
        every { authenticationManager.authenticate(any()) } returns auth
        every { jwtTokenProvider.generateToken(any()) } returns "test_token"

        // When/Then
        mockMvc.post("/api/auth/signin") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("test_token") }
            jsonPath("$.tokenType") { value("Bearer") }
        }
    }

    @Test
    fun `forgot password with existing email should return ok`() {
        // Given
        val email = "test@example.com"
        val user = User(
            username = "testuser",
            email = email,
            password = "hashed_password",
            name = "Test User"
        )

        every { userRepository.findByEmail(email) } returns Optional.of(user)
        every { passwordEncoder.encode(any<String>()) } returns "hashed_token"

        // When/Then
        mockMvc.post("/api/auth/forgot-password?email=$email") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.message") { value("If your email is found in our database, a reset message will be sent to your email address") }
        }
    }

    @Test
    fun `change password with valid token should return ok`() {
        // Given
        val email = "test@example.com"
        val resetToken = "test_token"
        val user = User(
            username = "testuser",
            email = email,
            password = "old_hashed_password",
            name = "Test User"
        )
        user.resetToken = "hashed_token"
        user.resetTokenCreationDate = LocalDateTime.now()

        val changePasswordDto = mapOf(
            "email" to email,
            "resetToken" to resetToken,
            "newPassword" to "new_password"
        )

        every { userRepository.findByEmail(email) } returns Optional.of(user)
        every { passwordEncoder.matches(resetToken, user.resetToken) } returns true
        every { passwordEncoder.encode(any<String>()) } returns "new_hashed_password"

        // When/Then
        mockMvc.post("/api/auth/change-password") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(changePasswordDto)
        }.andExpect {
            status { isOk() }
            jsonPath("$.message") { value("Password was changed successfully") }
        }
    }
}
