package presentation.model

sealed class PageMode {
    data object View : PageMode()
    data object Select : PageMode()
    data class Edit(val element: ElementUI) : PageMode()
}