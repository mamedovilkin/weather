package io.github.mamedovilkin.weather.domain.repository

import io.github.mamedovilkin.weather.domain.model.Forecast
import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.util.Result

interface NetworkRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Weather>

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Forecast>

    suspend fun searchLocation(
        query: String,
    ): Result<List<Location>>
}