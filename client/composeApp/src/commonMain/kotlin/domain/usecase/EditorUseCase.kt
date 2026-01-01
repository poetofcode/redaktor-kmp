package domain.usecase

import domain.repository.EditorRepository
import domain.model.Element
import domain.model.Page
import data.utils.CommonFlow
import data.utils.flowOnDefaultContext
import kotlinx.coroutines.flow.flow

class EditorUseCase constructor(
    private val repository: EditorRepository
) {
    fun fetchPages() : CommonFlow<List<Page>> = flow {
        emit(repository.fetchPages())
    }.flowOnDefaultContext()

    fun createPage() : CommonFlow<Unit> = flow {
        repository.createPage()
        emit(Unit)
    }.flowOnDefaultContext()

    fun updatePage(pageId: String, title: String) : CommonFlow<Unit> = flow {
        repository.updatePage(pageId, title)
        emit(Unit)
    }.flowOnDefaultContext()

    fun fetchPageById(pageId: String): CommonFlow<Page> = flow {
        emit(repository.fetchPageById(pageId))
    }.flowOnDefaultContext()

    fun fetchStartPage() : CommonFlow<Page> = flow {
        emit(repository.fetchStartPage())
    }.flowOnDefaultContext()

    fun createOrUpdateElement(pageId: String, element: Element): CommonFlow<Unit> = flow {
        repository.createOrUpdateElement(pageId, element)
        emit(Unit)
    }.flowOnDefaultContext()

    fun reorderElements(pageId: String, firstElementId: String, secondElementId: String): CommonFlow<Unit> = flow {
        repository.reorderElements(pageId, firstElementId, secondElementId)
        emit(Unit)
    }.flowOnDefaultContext()

    fun reorderPages(firstPageId: String, secondPageId: String): CommonFlow<Unit> = flow {
        repository.reorderPages(firstPageId, secondPageId)
        emit(Unit)
    }.flowOnDefaultContext()

    fun deleteElement(pageId: String, elementId: String): CommonFlow<Unit> = flow {
        repository.deleteElement(pageId, elementId)
        emit(Unit)
    }.flowOnDefaultContext()

    fun deletePage(pageId: String) : CommonFlow<Unit> = flow<Unit> {
        repository.deletePage(pageId)
        emit(Unit)
    }.flowOnDefaultContext()
}