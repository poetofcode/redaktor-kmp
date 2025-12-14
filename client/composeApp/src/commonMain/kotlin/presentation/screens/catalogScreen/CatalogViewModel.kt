package presentation.screens.catalogScreen

import data.utils.swap
import domain.usecase.EditorUseCase
import presentation.model.PageUI
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSharedEvent
import presentation.model.shared.OnPagesUpdatedEvent
import presentation.model.shared.OnPagePickedEvent
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.SharedEvent
import presentation.screens.pageScreen.PageIntent
import presentation.screens.pageScreen.PageScreen
import presentation.screens.pageScreen.misc.ElementType

class CatalogViewModel constructor(
    private val editorUseCase: EditorUseCase,
) : BaseViewModel<CatalogViewModel.CatalogState>() {

    data class CatalogState(
        val pages: List<PageUI> = emptyList(),
        val editablePage: PageUI? = null,
        val isPicker: Boolean = false,
        val draggableIndex: Int? = null,
    ) {
        val isEditing: Boolean get() = editablePage != null
    }

    init {
        fetchData()
    }

    private fun fetchData() {
        editorUseCase.fetchPages()
            .onEach { pages ->
                println("mylog Pages: ${pages}")

                reduce {
                    copy(
                        pages = pages.map { page ->
                            PageUI(
                                id = page.id,
                                title = page.title,
                            )
                        }
                    )
                }
            }
            .catch {
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun handleIntent(intent: CatalogIntent) {
        when (intent) {
            is CatalogIntent.OnPageClick -> {
                postEffect(
                    NavigateEffect(PageScreen(initialPageId = intent.pageId))
                )
            }

            CatalogIntent.OnAddPageClick -> {
                addNewPage()
            }

            is CatalogIntent.OnDeleteClick -> {
                // TODO implememnt
            }

            is CatalogIntent.OnEditClick -> {
                val selectedPage = state.value.pages.first { it.id == intent.pageId }
                reduce {
                    copy(
                        editablePage = selectedPage
                    )
                }
            }

            CatalogIntent.OnApplyEditClick -> {
                applyChanges()
            }

            CatalogIntent.OnCancelEditClick -> {
                reduce { copy(editablePage = null) }
            }

            is CatalogIntent.OnEditablePageChanged -> {
                reduce { copy(editablePage = intent.newPage) }
            }

            is CatalogIntent.PassParameter -> {
                reduce { copy(isPicker = intent.isPicker) }
            }

            is CatalogIntent.OnBindLink -> {
                postSharedEvent(OnPagePickedEvent(page = intent.page))
                postEffect(NavigateBackEffect())
            }

            CatalogIntent.OnFinishDragging -> {
                reduce { copy(draggableIndex = null) }
            }

            is CatalogIntent.OnReorderListElement -> {
                editorUseCase.reorderPages(
                    firstPageId = state.value.pages[intent.oldPosition].id,
                    secondPageId = state.value.pages[intent.newPosition].id,
                )
                    .catch { e ->
                        e.printStackTrace()
                        fetchData()
                    }
                    .launchIn(viewModelScope)

                reduce {
                    copy(
                        pages = pages.swap(intent.oldPosition, intent.newPosition),
                        draggableIndex = intent.newPosition,
                    )
                }
            }

            is CatalogIntent.OnStartDragging -> {
                reduce { copy(draggableIndex = intent.itemIndex) }
            }
        }
    }

    override fun obtainSharedEvent(event: SharedEvent) {
        super.obtainSharedEvent(event)

        when (event) {
            OnPagesUpdatedEvent -> {
                fetchData()
            }
        }
    }

    private fun applyChanges() {
        val updatedPage = state.value.editablePage?.copy() ?: return
        editorUseCase.updatePage(pageId = updatedPage.id, title = updatedPage.title)
            .onEach {
                reduce {
                    copy(
                        editablePage = null,
                        pages = state.value.pages.map {
                            if (it.id == updatedPage.id) {
                                updatedPage
                            } else {
                                it
                            }
                        }
                    )
                }
                postSharedEvent(OnPagesUpdatedEvent)
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun addNewPage() {
        editorUseCase.createPage()
            .onEach {
                fetchData()
                postSharedEvent(OnPagesUpdatedEvent)
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }

    override fun onInitState(): CatalogState = CatalogState()

}


sealed class CatalogIntent {
    object OnAddPageClick : CatalogIntent()
    object OnApplyEditClick : CatalogIntent()

    object OnCancelEditClick : CatalogIntent()

    class OnPageClick(val pageId: String) : CatalogIntent()
    class OnEditClick(val pageId: String) : CatalogIntent()

    class OnDeleteClick(val pageId: String) : CatalogIntent()
    class OnEditablePageChanged(val newPage: PageUI) : CatalogIntent()
    class PassParameter(val isPicker: Boolean) : CatalogIntent()
    class OnBindLink(val page: PageUI) : CatalogIntent()

    class OnReorderListElement(val oldPosition: Int, val newPosition: Int) : CatalogIntent()
    class OnStartDragging(val itemIndex: Int) : CatalogIntent()
    object OnFinishDragging : CatalogIntent()
}
