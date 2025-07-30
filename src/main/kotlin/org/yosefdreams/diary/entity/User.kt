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
    
    @Column(nullable = false, unique = true)
    val username: String,
    
    @Column(nullable = false)
    var password: String,
    
    @Column(nullable = false, unique = true)
    val email: String,
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    val roles: MutableSet<String> = mutableSetOf("USER"),
    
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,
    
    var isActive: Boolean = true
)
