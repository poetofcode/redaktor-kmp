package data.service

import data.utils.ProfileStorage
import io.ktor.client.HttpClient
import presentation.base.Config

interface NetworkingFactory {

    fun createHttpClient() : HttpClient

    fun createApi() : MainApi

}

class NetworkingFactoryImpl(
    private val profileStorage: ProfileStorage,
    private val deviceType: Config.DeviceTypes,
    private val appVersion: String = "0.1",
) : NetworkingFactory {
    
    override fun createHttpClient(): HttpClient {
        return HttpClientFactory(
            baseUrl = BASE_URL,
        ).createClient()
    }

    override fun createApi(): MainApi {
        return MainApi(
            httpClient = createHttpClient(),
            profileStorage = profileStorage,
            deviceType = deviceType,
            appVersion = appVersion,
        )
    }

    private companion object {
        // TODO вынести в buildConfig
        const val BASE_URL = "http://127.0.0.1:3000"
    }

}