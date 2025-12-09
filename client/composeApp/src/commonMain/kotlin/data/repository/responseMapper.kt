package data.repository

import data.entity.DataResponse
import data.entity.ExceptionResponse
import data.entity.FailureResponse
import data.entity.ResultResponse
import domain.model.SomeNetworkException

fun <T : Any> ResultResponse<T>.resultOrError(): T {
    return when (val response = this) {
        is DataResponse -> {
            response.result
        }

        is ExceptionResponse -> {
            throw SomeNetworkException(response.error.message.orEmpty())
        }

        is FailureResponse -> {
            throw SomeNetworkException(response.description)
        }
    }
}