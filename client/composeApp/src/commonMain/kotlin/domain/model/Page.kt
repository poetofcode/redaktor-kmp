package domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Page(
    @SerialName("id")
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("elements")
    val elements: List<Element>,

    @SerialName("is_new")
    var isNew: Boolean = false
) {
    companion object {
        fun createEmptyPage() : Page {
            return Page(
                id = String(),
                title = String(),
                elements = emptyList(),
            ).apply {
                isNew = true
            }
        }
    }
}