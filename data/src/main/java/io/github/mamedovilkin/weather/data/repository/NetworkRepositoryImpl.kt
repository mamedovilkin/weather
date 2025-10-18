package io.github.mamedovilkin.weather.data.repository

import io.github.mamedovilkin.weather.data.BuildConfig
import io.github.mamedovilkin.weather.data.dao.WeatherDao
import io.github.mamedovilkin.weather.data.entity.toDomainForecast
import io.github.mamedovilkin.weather.data.entity.toDomainWeather
import io.github.mamedovilkin.weather.data.model.ForecastDto
import io.github.mamedovilkin.weather.data.model.LocationDto
import io.github.mamedovilkin.weather.data.model.WeatherDto
import io.github.mamedovilkin.weather.data.model.toDomainLocation
import io.github.mamedovilkin.weather.data.model.toEntityForecast
import io.github.mamedovilkin.weather.data.model.toEntityWeather
import io.github.mamedovilkin.weather.domain.model.Forecast
import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.domain.util.Result
import io.github.mamedovilkin.weather.domain.util.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.util.Locale

class NetworkRepositoryImpl(
    private val httpClient: HttpClient,
    private val weatherDao: WeatherDao
) : NetworkRepository {

    val lang = if (Locale.getDefault().language == "ru") "ru" else "en"

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Weather> {
        try {
            val weatherEntity = httpClient
                .get("data/2.5/weather?lat=$lat&lon=$lon&lang=$lang&units=${temperatureUnit.name.lowercase()}&appid=${BuildConfig.APP_ID}")
                .body<WeatherDto>()
                .toEntityWeather()

            weatherDao.insertWeather(weatherEntity)

            val weather = weatherDao.getWeather()

            return Result.Success(weather.toDomainWeather())
        } catch (e: Exception) {
            try {
                val weather = weatherDao.getWeather()
                return Result.Success(weather.toDomainWeather())
            } catch (_: Exception) {
                return Result.Failure(e)
            }
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Forecast> {
        try {
            val forecastEntity = httpClient
                .get("data/2.5/forecast?lat=$lat&lon=$lon&lang=$lang&units=${temperatureUnit.name.lowercase()}&appid=${BuildConfig.APP_ID}")
                .body<ForecastDto>()
                .toEntityForecast()

            weatherDao.insertForecast(forecastEntity)

            val forecast = weatherDao.getForecast()

            return Result.Success(forecast.toDomainForecast())
        } catch (e: Exception) {
            try {
                val forecast = weatherDao.getForecast()
                return Result.Success(forecast.toDomainForecast())
            } catch (_: Exception) {
                return Result.Failure(e)
            }
        }
    }

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        val formattedQuery = query.replace(" ", "%20")

        return safeRequest {
            httpClient
                .get("geo/1.0/direct?q=$formattedQuery&lang=$lang&limit=5&appid=${BuildConfig.APP_ID}")
                .body<List<LocationDto>>()
                .map { it.toDomainLocation() }
        }
    }
}