package org.yosefdreams.diary.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ChangePasswordDto(
    @field:Email
    @field:NotBlank
    val email: String,
    
    @field:NotBlank
    val newPassword: String,
    
    @field:NotBlank
    val resetToken: String
)
