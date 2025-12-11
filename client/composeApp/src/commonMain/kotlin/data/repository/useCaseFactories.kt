package data.repository

import domain.usecase.EditorUseCase

interface UseCaseFactory {

    fun createEditorUseCase() : EditorUseCase

}

class UseCaseFactoryImpl(val repositoryFactory: RepositoryFactory) : UseCaseFactory {

    override fun createEditorUseCase(): EditorUseCase {
        return EditorUseCase(repositoryFactory.createEditorRepository())
    }

}