package org.yosefdreams.diary

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
abstract class BaseIntegrationTest {
    @BeforeEach
    fun setup() {
        // Common setup code for all tests
    }
}
