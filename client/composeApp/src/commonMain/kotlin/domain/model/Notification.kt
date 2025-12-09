package domain.model

data class Notification(
    val id: String,
    val createdAt: String,
    val title: String,
    val text: String,
    val image: String,
    val linkId: String,
    val extras: String,
    val seen: Boolean,
    val silent: Boolean
)
