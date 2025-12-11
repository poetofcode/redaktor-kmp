package presentation.screens.homeTabScreen

import domain.usecase.EditorUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.base.BaseViewModel

class HomeTabViewModel(
    private val editorUseCase: EditorUseCase,
) : BaseViewModel<Unit>() {

    override fun onInitState() {

    }

    init {
        val flow = editorUseCase.fetchStartPage()
        flow.onEach { page ->
            println("mylog: $page")
        }.catch { e ->
            e.printStackTrace()
        }.launchIn(viewModelScope)
    }

}