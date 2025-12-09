package data.service

import data.entity.CreateSessionRequestBody
import data.entity.CreateSessionResponse
import data.entity.DataResponse
import data.entity.ExceptionResponse
import data.entity.FailureResponse
import data.entity.JokesResponse
import data.entity.NotificationResponse
import data.entity.ResultResponse
import data.entity.SaveFirebasePushTokenRequestBody
import data.entity.SubscriptionResponse
import data.utils.ProfileStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import presentation.base.Config

class MainApi(
    private val httpClient: HttpClient,
    private val profileStorage: ProfileStorage,
    private val deviceType: Config.DeviceTypes,
    private val appVersion: String,
) {

    suspend fun fetchJokes() = parseRequestResult<JokesResponse> {
        httpClient.get {
            authBlock {
                url {
                    path("/api/v1/jokes")
                }
            }
        }
    }

    suspend fun createSession(requestBody: CreateSessionRequestBody): ResultResponse<CreateSessionResponse> =
        parseRequestResult<CreateSessionResponse> {
            httpClient.post {
                nonAuthBlock {
                    url { path("/api/v1/sessions") }
                    setBody(requestBody)
                }
            }
        }

    suspend fun deleteSession(token: String) {
        httpClient.delete {
            authBlock {
                url { path("/api/v1/sessions/$token") }
            }
        }
    }

    suspend fun createSubscription() {
        httpClient.post {
            authBlock {
                url { path("/api/v1/subscriptions") }
            }
        }
    }

    suspend fun deleteSubscription() {
        httpClient.delete {
            authBlock {
                url { path("/api/v1/subscriptions") }
            }
        }
    }

    suspend fun getSubscription() = parseRequestResult<SubscriptionResponse> {
        httpClient.get {
            authBlock {
                url { path("/api/v1/subscriptions/me") }
            }
        }
    }

    suspend fun getNotifications() = parseRequestResult<NotificationResponse> {
        httpClient.get {
            authBlock {
                url { path("/api/v1/notifications") }
            }
        }
    }

    suspend fun saveFirebasePushToken(pushToken: String) {
        httpClient.post {
            authBlock {
                url { path("/api/v1/sessions/push_token") }
                setBody(SaveFirebasePushTokenRequestBody(
                    pushToken = pushToken
                ))
            }
        }
    }

    suspend fun markNotificationsAsSeen() {
        httpClient.post {
            authBlock {
                url { path("/api/v1/users/me/notifications/seen/") }
            }
        }
    }

    private suspend inline fun <reified T : Any> parseRequestResult(doRequest: () -> HttpResponse): ResultResponse<T> {
        var response: HttpResponse? = null
        val parsed: ResultResponse<T> = try {
            response = doRequest()
            response.body<DataResponse<T>>()
        } catch (e: Throwable) {
            response?.tryParseFailure() ?: ExceptionResponse(e)
        }
        return parsed
    }

    private suspend inline fun <reified T : Any> HttpResponse.tryParseFailure(): FailureResponse<T>? =
        try {
            body<FailureResponse<T>>()
        } catch (e: Throwable) {
            null
        }

    private fun HttpRequestBuilder.nonAuthBlock(block: HttpRequestBuilder.() -> Unit) {
        contentType(ContentType.Application.Json)
        header("x-client-type", deviceType.title)
        header("x-client-version", appVersion)
        block()
    }

    private fun HttpRequestBuilder.authBlock(block: HttpRequestBuilder.() -> Unit) {
        nonAuthBlock {
            profileStorage.load()?.let {
                bearerAuth(it.token)
            }
        }
        block()
    }

    suspend fun createAccount(requestBody: CreateSessionRequestBody) {
        httpClient.post {
            nonAuthBlock {
                url { path("/api/v1/users") }
                setBody(requestBody)
            }
        }
    }

}