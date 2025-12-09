package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val token: String,
    val email: String,
)