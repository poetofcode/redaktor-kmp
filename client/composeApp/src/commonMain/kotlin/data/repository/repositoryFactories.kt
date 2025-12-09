package data.repository

import data.service.MainApi
import data.utils.ProfileStorage

interface RepositoryFactory {

    fun createJokeRepository() : JokeRepository

    fun createProfileRepository(): ProfileRepository

}

class RepositoryFactoryImpl(
    val api: MainApi,
    val profileStorage: ProfileStorage,
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

}