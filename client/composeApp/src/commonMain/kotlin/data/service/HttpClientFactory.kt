package data.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(
    val baseUrl: String,
) {
    
    fun createClient() : HttpClient {
        val bearerTokenStorage = mutableListOf<BearerTokens>()

//        fun calculateToken(path: String, remoteToken: String, salt: String) : String {
//            return (path.toSha1() + remoteToken.toSha1() + salt.toSha1()).toSha1()
//        }

        bearerTokenStorage.add(BearerTokens("", ""))

        return HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                // filter { request ->
                //    request.url.host.contains("ktor.io")
                // }
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(DefaultRequest) {
                url(baseUrl)
            }

//            install(Auth) {
//                bearer {
//                    loadTokens {
//                        bearerTokenStorage.last()
//                    }

//                    refreshTokens { // this: RefreshTokensParams
//                        try {
//                            val tokenResponse: DataResponse<TokenResponse> = client.post("/site/token".buildEndpoint(baseUrl)).body()
//                            val remoteToken : String = tokenResponse.result.token!!
//
//                            val newToken : String = run {
//                                val path = response.request.url.encodedPath
//                                val t = calculateToken(path, remoteToken, apiKey)
//                                t
//                            }
//                            bearerTokenStorage.add(BearerTokens(newToken, remoteToken))
//                        } catch (e: Throwable) {
//                            e.printStackTrace()
//                        }
//
//                        bearerTokenStorage.last()
//                    }

//                }
//            }

        }
    }
    
}