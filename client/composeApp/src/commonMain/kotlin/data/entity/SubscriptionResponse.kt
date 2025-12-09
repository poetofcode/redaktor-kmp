package data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionResponse(
    @SerialName("isSubscribed") val isSubscribed: Boolean,
)
