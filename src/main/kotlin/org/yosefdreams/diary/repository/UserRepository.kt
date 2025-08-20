package org.yosefdreams.diary.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.yosefdreams.diary.entity.User
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun findByUsernameOrEmail(username: String, email: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
