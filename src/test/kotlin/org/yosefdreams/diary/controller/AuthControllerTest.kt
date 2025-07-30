package org.yosefdreams.diary.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.yosefdreams.diary.BaseIntegrationTest
import org.yosefdreams.diary.dto.LoginRequest
import org.yosefdreams.diary.dto.RegisterRequest
import org.yosefdreams.diary.entity.User
import org.yosefdreams.diary.repository.UserRepository
import java.util.*

@AutoConfigureMockMvc
class AuthControllerTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var authenticationManager: AuthenticationManager

    @Test
    fun `register user with valid data should return ok`() {
        // Given
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123"
        )

        every { userRepository.existsByUsername(any()) } returns false
        every { userRepository.existsByEmail(any()) } returns false
        every { userRepository.save(any()) } returns User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = "hashed_password"
        )

        // When/Then
        mockMvc.post("/api/auth/register") {
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
            password = "password123"
        )

        every { userRepository.existsByUsername(registerRequest.username) } returns true

        // When/Then
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("Username is already taken!") }
        }
    }

    @Test
    fun `login with valid credentials should return ok`() {
        // Given
        val loginRequest = LoginRequest(
            username = "testuser",
            password = "password123"
        )

        val auth = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        every { authenticationManager.authenticate(any()) } returns auth

        // When/Then
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.message") { value("User logged in successfully") }
        }
    }
}
