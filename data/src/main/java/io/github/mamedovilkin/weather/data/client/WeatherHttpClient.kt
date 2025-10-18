package io.github.mamedovilkin.weather.data.client

import io.github.mamedovilkin.weather.data.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WeatherHttpClient {

    companion object {
        @Volatile
        private var INSTANCE: HttpClient? = null

        fun getInstance(): HttpClient {
            return INSTANCE ?: synchronized(this) {
                val instance = HttpClient {
                    defaultRequest { url(BuildConfig.BASE_URL) }

                    install(Logging) { logger = Logger.SIMPLE }

                    install(ContentNegotiation) {
                        json(Json{
                            ignoreUnknownKeys = true
                        })
                    }
                }

                INSTANCE = instance

                instance
            }
        }
    }
}