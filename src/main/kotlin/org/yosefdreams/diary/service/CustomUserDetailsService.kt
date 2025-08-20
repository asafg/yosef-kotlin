package org.yosefdreams.diary.service

import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.yosefdreams.diary.entity.User
import org.yosefdreams.diary.repository.UserRepository

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsernameOrEmail(username, username)
            .orElseThrow {
                logger.error("User not found with username or email: {}", username)
                UsernameNotFoundException("User not found with username or email: $username")
            }

        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            emptyList() // We'll implement authorities later
        )
    }

    fun loadUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow {
                logger.error("User not found with id: {}", id)
                UsernameNotFoundException("User not found with id: $id")
            }
    }
}
