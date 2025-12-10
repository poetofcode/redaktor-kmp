package domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
abstract class Element {
    abstract var id: String

    @SerialName("is_new")
    var isNew: Boolean = false
}

//
// При добавлении новых подклассов - добавить запись в SerializersModule
// класса FileEditorRepository
//

@Serializable
data class TextElement(
    override var id: String,

    @SerialName("text")
    val text: String

) : Element() {
    companion object {
        fun createEmpty() = TextElement(
            id = String(),
            text = String(),
        ).apply { isNew = true }
    }
}

@Serializable
data class LinkElement(
    override var id: String,

    @SerialName("text")
    val text: String,

    @SerialName("related_page")
    val relatedPage: Page? = null,
) : Element() {

    companion object {
        fun createEmpty(): Element = LinkElement(
            id = String(),
            text = String(),
            relatedPage = null,
        )
    }
}