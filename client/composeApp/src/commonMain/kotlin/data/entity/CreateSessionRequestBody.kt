package data.entity

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequestBody(
    val login: String,
    val password: String,
)


