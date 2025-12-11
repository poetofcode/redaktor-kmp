package data.repository

import data.service.MainApi
import data.utils.ContentProvider
import data.utils.ProfileStorage
import domain.repository.EditorRepository

interface RepositoryFactory {

    fun createJokeRepository() : JokeRepository

    fun createProfileRepository(): ProfileRepository

    fun createEditorRepository(): EditorRepository

}

class RepositoryFactoryImpl(
    val api: MainApi,
    val profileStorage: ProfileStorage,
    val editorContentProvider: ContentProvider,
) : RepositoryFactory {

    override fun createJokeRepository(): JokeRepository {
        return JokeRepositoryImpl(api)
    }

    override fun createProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl(
            api = api,
            storage = profileStorage
        )
    }

    override fun createEditorRepository(): EditorRepository {
        return FileEditorRepository(editorContentProvider)
    }

}


