package org.yosefdreams.diary

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UnitTest {
    
    @Test
    fun `test password encoding`() {
        val passwordEncoder = BCryptPasswordEncoder()
        val rawPassword = "test123"
        val encodedPassword = passwordEncoder.encode(rawPassword)
        
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "Password should match after encoding")
    }
}
