package org.yosefdreams.diary.dto

data class JwtAuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)
