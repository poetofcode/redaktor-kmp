package domain.factory

import domain.usecase.EditorUseCase

interface UseCaseFactory {

    fun createEditorUseCase() : EditorUseCase

}