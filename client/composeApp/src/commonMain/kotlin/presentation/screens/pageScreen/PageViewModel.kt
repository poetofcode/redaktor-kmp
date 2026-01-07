package presentation.screens.pageScreen

import data.utils.swap
import domain.model.Element
import domain.model.LinkElement
import domain.model.Page
import domain.model.TextElement
import domain.usecase.EditorUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSideEffect
import presentation.model.ActionUI
import presentation.model.ElementUI
import presentation.model.PageMode
import presentation.model.PageUI
import presentation.model.shared.OnPagePickedEvent
import presentation.model.shared.OnPagesUpdatedEvent
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.OnScrollToNewElementEffect
import presentation.navigation.SharedEvent
import presentation.screens.catalogScreen.CatalogScreen
import presentation.screens.pageScreen.misc.ElementType

class PageViewModel constructor(
    val editorUseCase: EditorUseCase,
) : BaseViewModel<PageViewModel.PageState>() {

    var pageId: String? = null
        set(value) {
            field = value
            fetchPageData()
        }


    data class PageState(
        val pageId: String? = null,
        val pageTitle: String? = null,
        val textState: String = "Page UI state",
        val elements: List<ElementUI> = emptyList(),
        val mode: PageMode = PageMode.View,
        val draggableIndex: Int? = null,
        val elementType: ElementType = ElementType.TEXT,
    ) {
        val isDragging: Boolean get() = draggableIndex != null
    }

    init {
        // fetchPageData()
    }

    fun handleIntent(intent: PageIntent) {
        when (intent) {
            PageIntent.ToSampleScreen -> {
                // postEffect(NavigateEffect(SimpleScreen))
            }
            PageIntent.OnFinishEditModeClick -> {
                reduce { copy(mode = PageMode.View) }
            }
            PageIntent.OnStartEditModeClick -> {
                reduce { copy(mode = PageMode.Select) }
            }
            is PageIntent.OnActionClick -> {
                handleActionClick(intent.element, intent.action)
            }
            PageIntent.OnApplyElementChangesClick -> {
                applyElementChanges()
            }
            PageIntent.OnDiscardChangesElementClick -> {
                reduce {
                    copy(
                        mode = PageMode.Select,
                    )
                }
            }
            is PageIntent.OnEditableElementChanged -> {
                reduce {
                    copy(
                        mode = PageMode.Edit(intent.updatedElement)
                    )
                }
            }
            is PageIntent.OnElementClick -> {
                when (val element = intent.element) {
                    is ElementUI.Link -> {
                        element.relatedPage?.let { page ->
                            postEffect(NavigateEffect(
                                screen = PageScreen(initialPageId = page.id),
                            ))
                        }
                    }

                    else -> Unit
                }
            }

            PageIntent.OnAddNewElementClick -> {
                onAddNewElementClick()
            }
            is PageIntent.OnReorderListElement -> {
                editorUseCase.reorderElements(
                    pageId = state.value.pageId ?: return,
                    firstElementId = state.value.elements[intent.oldPosition].id,
                    secondElementId = state.value.elements[intent.newPosition].id,
                )
                    .catch { e ->
                        e.printStackTrace()
                        fetchPageData()
                    }
                    .launchIn(viewModelScope)

                reduce {
                    copy(
                        elements = elements.swap(intent.oldPosition, intent.newPosition),
                        draggableIndex = intent.newPosition,
                    )
                }
            }
            PageIntent.OnFinishDragging -> reduce { copy(draggableIndex = null) }
            is PageIntent.OnStartDragging -> reduce { copy(draggableIndex = intent.itemIndex) }

            is PageIntent.OnSelectElementType -> {
                reduce { copy(elementType = intent.elementType) }
            }

            PageIntent.OnStartReorderModeClick -> {
                reduce { copy(mode = PageMode.Reordering) }
            }
        }
    }


    override fun obtainSharedEvent(event: SharedEvent) {
        when (event) {
            is OnPagePickedEvent -> {
                val currentMode = state.value.mode
                if (currentMode !is PageMode.Edit) {
                    return
                }
                val editableElement = currentMode.element
                if (editableElement !is ElementUI.Link) {
                    return
                }
                reduce {
                    copy(
                        mode = PageMode.Edit(
                            element = editableElement.copy(relatedPage = event.page)
                        )
                    )
                }
                applyElementChanges()
            }

            OnPagesUpdatedEvent -> {
                fetchPageData()
            }

            else -> Unit
        }
    }

    
    private fun applyElementChanges() {
        when (val mode = state.value.mode) {
            is PageMode.Edit -> {
                val editableElement = mode.element
                editorUseCase.createOrUpdateElement(
                    pageId = state.value.pageId ?: return,
                    element = toElementApi(editableElement)
                ).onEach {
                    fetchPageData()
                    reduce {
                        copy(
                            mode = PageMode.Select,
                        )
                    }
                }.catch {
                    it.printStackTrace()
                }.launchIn(viewModelScope)
            }
            else -> return
        }
    }

    private fun onAddNewElementClick() {
        val elementToAdd = when (state.value.elementType) {
            ElementType.TEXT -> TextElement.createEmpty()
            ElementType.LINK -> LinkElement.createEmpty()
        }
        addNewElementToPage(elementToAdd)
    }

    private fun addNewElementToPage(element: Element) {
        editorUseCase.createOrUpdateElement(state.value.pageId ?: return, element)
            .onEach {
                fetchPageData()
                postSideEffect(OnScrollToNewElementEffect(
                    elementPosition = state.value.elements.size - 1
                ))

                // TODO открывать сразу экран редактирования элемента после доабвления
//                reduce {
//                    val elementUi = elements.firstOrNull { it.id == element.id }
//                    copy(
//                        mode = PageMode.Edit(elementUi ?: return@reduce state.value),
//                    )
//                }
            }
            .catch { e -> e.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun fetchPageData() {
        val fetchPageFlow = if (pageId != null) {
            editorUseCase.fetchPageById(pageId!!)
        } else {
            editorUseCase.fetchStartPage()
        }
        fetchPageFlow
            .onEach { page ->
                val elementsUI = fromElementsApi(page.elements)
                reduce {
                    copy(
                        pageId = page.id,
                        pageTitle = page.title,
                        elements = elementsUI
                    )
                }
            }
            .catch { e -> e.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun deleteElement(element: ElementUI) {
        editorUseCase.deleteElement(state.value.pageId ?: return, element.id)
            .onEach { fetchPageData() }
            .catch { e -> e.printStackTrace() }
            .launchIn(viewModelScope)
    }

    private fun handleActionClick(element: ElementUI, action: ActionUI) {
        when (action) {
            ActionUI.Delete -> {
                deleteElement(element)
            }
            ActionUI.Edit -> {
                reduce {
                    copy(
                        mode = PageMode.Edit(element),
                    )
                }
            }
            ActionUI.BindLink -> {
                postEffect(NavigateEffect(CatalogScreen(isPicker = true)))
            }

            ActionUI.Copy -> Unit
        }
    }


    private fun fromElementsApi(items: List<Element>): List<ElementUI> {
        return items.mapNotNull {
            when (val element = it) {
                is TextElement -> {
                    ElementUI.Text(text = element.text, id = element.id)
                }

                is LinkElement -> {
                    ElementUI.Link(
                        text = element.text,
                        id = element.id,
                        relatedPage = element.relatedPage?.run {
                            PageUI(id = this.id, title = this.title)
                        },
                    )
                }
                else -> null
            }
        }
    }

    private fun toElementApi(elementUi: ElementUI): Element {
        return when (elementUi) {
            is ElementUI.Text -> TextElement(
                id = elementUi.id,
                text = elementUi.text,
            )
            is ElementUI.Link -> LinkElement(
                id = elementUi.id,
                text = elementUi.text,
                relatedPage = if (elementUi.relatedPage != null) {
                    Page(
                        id = elementUi.relatedPage.id,
                        title = elementUi.relatedPage.title,
                        elements = emptyList(),
                    )
                } else null,
            )
        }
    }

    fun onBackPress() {
        postEffect(NavigateBackEffect())
    }

    override fun onInitState(): PageState = PageState()

}

sealed class PageIntent {
    data class OnActionClick(val element: ElementUI, val action: ActionUI) : PageIntent()
    data class OnEditableElementChanged(val updatedElement: ElementUI) : PageIntent()
    data class OnElementClick(val element: ElementUI) : PageIntent()
    class OnReorderListElement(val oldPosition: Int, val newPosition: Int) : PageIntent()

    class OnStartDragging(val itemIndex: Int) : PageIntent()
    class OnSelectElementType(val elementType: ElementType) : PageIntent()

    object OnFinishDragging : PageIntent()

    object ToSampleScreen : PageIntent()
    object OnStartEditModeClick : PageIntent()
    object OnFinishEditModeClick : PageIntent()
    object OnApplyElementChangesClick : PageIntent()
    object OnDiscardChangesElementClick : PageIntent()
    object OnAddNewElementClick : PageIntent()
    object OnStartReorderModeClick : PageIntent()
}