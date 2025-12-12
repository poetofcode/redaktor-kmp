package presentation.screens.catalog

import domain.usecase.EditorUseCase
import presentation.model.PageUI
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.base.BaseViewModel

class CatalogViewModel constructor(
    private val editorUseCase: EditorUseCase,
) : BaseViewModel<CatalogViewModel.CatalogState>() {

    data class CatalogState(
        val pages: List<PageUI> = emptyList(),
        val editablePage: PageUI? = null,
        val isPicker: Boolean = false,
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

                /*
                updateState {
                    copy(
                        pages = pages.map { page ->
                            PageUI(
                                id = page.id,
                                title = page.title,
                            )
                        }
                    )
                }
                 */

            }
            .catch {
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun handleIntent(intent: CatalogIntent) {
        /*
        when (intent) {
            is CatalogIntent.OnPageClick -> {
                offerEffect(
                    effect = NavigationEffect.Navigate(
                        RootScreen.PageScreen.withArguments("pageId" to intent.pageId)
                    )
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
                updateState {
                    copy(
                        editablePage = selectedPage
                    )
                }
            }
            CatalogIntent.OnApplyEditClick -> {
                applyChanges()
            }
            CatalogIntent.OnCancelEditClick -> {
                updateState { copy(editablePage = null) }
            }
            is CatalogIntent.OnEditablePageChanged -> {
                updateState { copy(editablePage = intent.newPage) }
            }
            is CatalogIntent.PassParameter -> {
                updateState { copy(isPicker = intent.isPicker) }
            }
            is CatalogIntent.OnBindLink -> {
                offerIntermediateEffect(OnPagePickedEffect(page = intent.page))
                offerEffect(NavigationEffect.NavigateUp)
            }
        }

         */
    }

    /*
    override fun handleIntermediateEffect(effect: Effect) {
        when (effect) {
            OnPagesUpdatedEffect -> {
                fetchData()
            }
        }
    }
     */

    /*
    private fun applyChanges() {
        val updatedPage = state.value.editablePage?.copy() ?: return
        editorUseCase.updatePage(pageId = updatedPage.id, title = updatedPage.title)
            .onEach {
                updateState {
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
                offerIntermediateEffect(OnPagesUpdatedEffect)
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }
     */

    /*
    private fun addNewPage() {
        editorUseCase.createPage()
            .onEach {
                fetchData()
                offerIntermediateEffect(OnPagesUpdatedEffect)
            }
            .catch { it.printStackTrace() }
            .launchIn(viewModelScope)
    }
     */

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
}
