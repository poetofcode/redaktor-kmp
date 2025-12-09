package data.repository

import data.service.MainApi
import domain.model.JokeModel

interface JokeRepository {

    suspend fun fetchJokes(): List<JokeModel>

}

class JokeRepositoryImpl(val api: MainApi) : JokeRepository {

    override suspend fun fetchJokes(): List<JokeModel> {
        return api.fetchJokes()
            .resultOrError()
            .items
            .orEmpty()
            .map { item ->
                JokeModel(
                    text = item.text.orEmpty(),
                    id = item.id.orEmpty(),
                )
            }
    }

}
