package org.yosefdreams.diary.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(unique = true, nullable = false)
    val username: String,
    
    @Column(unique = true, nullable = false)
    val email: String,
    
    @Column(nullable = false)
    var password: String,
    
    @Column(nullable = false)
    var name: String,
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    val roles: MutableSet<String> = mutableSetOf("ROLE_USER"),
    
    @Column(name = "reset_token")
    var resetToken: String? = null,
    
    @Column(name = "reset_token_creation_date")
    var resetTokenCreationDate: LocalDateTime? = null,
    
    var isActive: Boolean = true
) {
    fun addRole(role: String) {
        roles.add(role)
    }
    
    fun removeRole(role: String) {
        roles.remove(role)
    }
}
