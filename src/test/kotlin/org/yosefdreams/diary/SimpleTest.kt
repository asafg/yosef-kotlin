package org.yosefdreams.diary

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [TestConfig::class])
class SimpleTest {
    
    @Test
    fun `simple test`() {
        assert(true)
    }
}
