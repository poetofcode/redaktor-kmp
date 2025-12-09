package data.entity

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionResponse(
    val token: String,
    val user: UserDTO,
)


@Serializable
data class UserDTO(
    val login: String,
)