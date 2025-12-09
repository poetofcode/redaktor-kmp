package data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JokesResponse(
    var items: List<Joke>? = null,
) {

    @Serializable
    data class Joke(
        @SerialName("_id") var id: String?,
        var text: String? = null
    )
}