package data.entity

import kotlinx.serialization.Serializable

@Serializable
sealed class ResultResponse<T>(
    val isError: Boolean = false
)

@Serializable
data class DataResponse<T>(
    val result: T,
) : ResultResponse<T>(false)


data class FailureResponse<T>(
    val status: Int,
    val code: String,
    val description: String,
) : ResultResponse<T>(true)


data class ExceptionResponse<T>(
    val error: Throwable,
) : ResultResponse<T>(true)