package data.utils

import kotlinx.serialization.json.Json

object JsonProvider {

    val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

}