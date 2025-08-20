package org.yosefdreams.diary.testutil

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.yosefdreams.diary.dto.LoginRequest
import org.yosefdreams.diary.dto.SignupRequest

object TestUtils {
    
    private val objectMapper = ObjectMapper()
    
    fun MockMvc.performLogin(email: String, password: String): ResultActions {
        val loginRequest = LoginRequest(email, password)
        return this.perform(
            MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
    }
    
    fun MockMvc.performSignup(
        username: String,
        email: String,
        password: String,
        name: String = "Test User"
    ): ResultActions {
        val signupRequest = SignupRequest(username, email, password, name)
        return this.perform(
            MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )
    }
    
    inline fun <reified T> parseResponse(result: ResultActions): T {
        val response = result.andReturn().response.contentAsString
        return objectMapper.readValue(response)
    }
    
    fun toJson(obj: Any): String = objectMapper.writeValueAsString(obj)
}
