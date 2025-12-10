package domain.factory

import data.repository.MockEditorRepository
import domain.usecase.EditorUseCase

class MockUseCaseFactory : UseCaseFactory {
    override fun createEditorUseCase(): EditorUseCase {
        return EditorUseCase(repository = MockEditorRepository())
    }
}