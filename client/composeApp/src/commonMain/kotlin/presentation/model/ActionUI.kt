package presentation.model

sealed class ActionUI {
    object Edit : ActionUI()
    object Delete : ActionUI()
    object BindLink : ActionUI()

    companion object {
        val BY_DEFAULT: List<ActionUI> = emptyList()
    }
}