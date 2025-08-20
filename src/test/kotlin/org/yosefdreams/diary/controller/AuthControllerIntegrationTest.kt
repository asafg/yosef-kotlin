package org.yosefdreams.diary.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.yosefdreams.diary.BaseIntegrationTest
import org.yosefdreams.diary.testutil.TestUtils

class AuthControllerIntegrationTest : BaseIntegrationTest() {

    private val testUsername = "testuser"
    private val testEmail = "test@example.com"
    private val testPassword = "Test@1234"
    private val testName = "Test User"

    @Test
    fun `test successful registration`() {
        // When & Then
        TestUtils.performSignup(mockMvc, testUsername, testEmail, testPassword, testName)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("User registered successfully!"))
    }

    @Test
    fun `test successful login`() {
        // Given
        TestUtils.performSignup(mockMvc, testUsername, testEmail, testPassword, testName)

        // When & Then
        TestUtils.performLogin(mockMvc, testEmail, testPassword)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
    }

    @Test
    fun `test login with invalid credentials`() {
        // When & Then
        TestUtils.performLogin(mockMvc, "nonexistent@example.com", "wrongpassword")
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `test registration with existing email`() {
        // Given
        TestUtils.performSignup(mockMvc, testUsername, testEmail, testPassword, testName)

        // When & Then
        TestUtils.performSignup(mockMvc, "anotheruser", testEmail, "Another@1234", "Another User")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `test registration with existing username`() {
        // Given
        TestUtils.performSignup(mockMvc, testUsername, testEmail, testPassword, testName)

        // When & Then
        TestUtils.performSignup(mockMvc, testUsername, "another@example.com", "Another@1234", "Another User")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `test registration with invalid data`() {
        // When & Then
        TestUtils.performSignup(mockMvc, "", "invalid-email", "short", "")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors").exists())
    }
}
