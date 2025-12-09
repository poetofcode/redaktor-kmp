package data.entity

import kotlinx.serialization.Serializable

@Serializable
data class SaveFirebasePushTokenRequestBody(
    val pushToken: String
)