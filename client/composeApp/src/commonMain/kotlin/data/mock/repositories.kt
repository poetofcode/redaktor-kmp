package data.mock

import data.repository.JokeRepository
import domain.model.JokeModel

class MockJokeRepository : JokeRepository {
    
    override suspend fun fetchJokes(): List<JokeModel> {
        return listOf()
    }

}