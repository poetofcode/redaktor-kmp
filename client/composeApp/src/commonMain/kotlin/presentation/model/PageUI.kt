package presentation.model

data class PageUI(
    val id: String = "",
    val title: String = "",
) {
    val isNew: Boolean get() = title.isBlank()
}