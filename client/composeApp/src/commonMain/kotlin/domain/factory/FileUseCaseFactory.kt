package domain.factory

import domain.usecase.EditorUseCase

class FileUseCaseFactory : UseCaseFactory {
    override fun createEditorUseCase(): EditorUseCase {
        // return EditorUseCase(SharedConfig.INSTANCE.editorRepository)
        return TODO("pass editorRepository impl")
    }
}