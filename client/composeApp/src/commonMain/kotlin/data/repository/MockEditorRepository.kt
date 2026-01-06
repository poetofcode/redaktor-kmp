package data.repository

import domain.repository.EditorRepository
import domain.model.Element
import domain.model.LinkElement
import domain.model.Page
import domain.model.TextElement

internal class MockEditorRepository : EditorRepository {
    private var UNIQ_ID_CURRENT = 99

    private var testPage = Page(
        id = "1",
        title = "Test page",
        elements = listOf(
            TextElement(
                id = "2",
                text = "Redaktor - блокнот с возможностями:"
            ),
            TextElement(
                id = "3",
                text = "Создание иерархических ссылок"
            ),
            TextElement(
                id = "4",
                text = "Возможности работать без сети/локально"
            ),
            LinkElement(
                id = "6",
                text = "Открыть page2",
                relatedPage = null,
            ),
            TextElement(
                id = "5",
                text = "Минималистичным интерфейсом (идея = больше контента - лучше)"
            ),
            LinkElement(
                id = "7",
                text = "Ссылка без привязки",
                relatedPage = null,
            ),
        )
    )
    private var testPage2 = Page(
        id = "2",
        title = "Test page 2",
        elements = listOf(
            TextElement(
                id = "21",
                text = "Заголовок страницы 2"
            ),
            LinkElement(
                id = "22",
                text = "Открыть page1",
                relatedPage = null,
            ),
        )
    )
    var pages = listOf(testPage, testPage2)

    override suspend fun fetchStartPage(): Page {
        return fetchPageById(pageId = fetchStartPageId())
    }

    override suspend fun fetchPageById(pageId: String): Page {
        return pages.first { it.id == pageId }
    }

    override suspend fun fetchPages(): List<Page> {
        TODO("Not yet implemented")
    }

    private suspend fun fetchStartPageId(): String {
        return "1"
    }

    override suspend fun createOrUpdateElement(pageId: String, element: Element) {
        var found = pages.first { it.id == pageId }
        if (found.elements.indexOfFirst { it.id == element.id } < 0) {
            found = found.copy(
                elements = found.elements.toMutableList().apply { add(element.run {
                    id = getUniqElementId()
                    return@run this
                }) }.toList()
            )
        } else {
            found = found.copy(
                elements = found.elements.map { item ->
                    if (item.id != element.id) {
                        item
                    } else {
                        element
                    }
                }
            )
        }
        pages = pages.map {
            if (it.id == pageId) {
                found
            } else {
                it
            }
        }
    }

    private fun getUniqElementId(): String {
        UNIQ_ID_CURRENT += 1
        return "${UNIQ_ID_CURRENT}"
    }

    override suspend fun deleteElement(pageId: String, elementId: String) {
        TODO("Not yet implemented")

    }

    override suspend fun deletePage(pageId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun reorderElements(pageId: String, firstElementId: String, secondElementId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun reorderPages(firstPageId: String, secondPageId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun createPage() {
        TODO("Not yet implemented")
    }

    override suspend fun updatePage(pageId: String, title: String) {
        TODO("Not yet implemented")
    }

    override suspend fun loadDBContent(): String {
        TODO("Not yet implemented")
    }

    override suspend fun saveDBContent(dbContent: String) {
        TODO("Not yet implemented")
    }

}