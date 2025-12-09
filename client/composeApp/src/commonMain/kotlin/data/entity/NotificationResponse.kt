package data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val items: List<Notification> = emptyList(),
) {

    @Serializable
    data class Notification(
        @SerialName("_id")
        val id: String,
        val createdAt: String,
        val title: String,
        val text: String,
        val image: String,
        val linkId: String,
        val extras: String,
        val seen: Boolean? = null,
        val silent: Boolean? = null,
    )
}


/*
    _id
    668d415fbe48f8f2af8ea510
    createdAt
    2024-07-09T14:00:13.759+00:00
    extras
    ""
    image
    ""
    linkId
    "999"
    text
    "Test text"
    title
    "Test title"
    userId
    123456789012345678901234
 */