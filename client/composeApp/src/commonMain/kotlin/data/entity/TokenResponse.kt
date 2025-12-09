package data.entity

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String? = null
)