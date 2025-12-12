package presentation.model

sealed class ElementUI(
    open val id: String,
    open val actions: List<ActionUI> = ActionUI.BY_DEFAULT,
) {

    data class Text(
        override val id: String,
        val text: String,
    ) : ElementUI(id)

    data class Link(
        override val id: String,
        val text: String,
        val relatedPage: PageUI?,
    ) : ElementUI(id) {
        override val actions: List<ActionUI> = listOf(ActionUI.BindLink)

        val isBound: Boolean
            get() {
                return relatedPage != null
            }
    }

}