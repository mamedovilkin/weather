package io.github.mamedovilkin.weather.network.repository

import io.github.mamedovilkin.weather.network.BuildConfig
import io.github.mamedovilkin.weather.network.client.Result
import io.github.mamedovilkin.weather.network.client.safeRequest
import io.github.mamedovilkin.weather.network.model.Location
import io.github.mamedovilkin.weather.network.model.Forecast
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.Weather
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.util.Locale

class NetworkRepositoryImpl(
    private val httpClient: HttpClient
) : NetworkRepository {

    val lang = if (Locale.getDefault().language == "ru") "ru" else "en"

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Weather> {
        return safeRequest {
            httpClient.get("data/2.5/weather?lat=$lat&lon=$lon&lang=$lang&units=${temperatureUnit.name.lowercase()}&appid=${BuildConfig.APP_ID}").body<Weather>()
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Forecast> {
        return safeRequest {
            httpClient.get("data/2.5/forecast?lat=$lat&lon=$lon&lang=$lang&units=${temperatureUnit.name.lowercase()}&appid=${BuildConfig.APP_ID}").body<Forecast>()
        }
    }

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        val formattedQuery = query.replace(" ", "%20")

        return safeRequest {
            httpClient.get("geo/1.0/direct?q=$formattedQuery&lang=$lang&limit=5&appid=${BuildConfig.APP_ID}").body<List<Location>>()
        }
    }
}