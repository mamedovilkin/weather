package io.github.mamedovilkin.weather.network.repository

import io.github.mamedovilkin.weather.network.client.Result
import io.github.mamedovilkin.weather.network.model.Location
import io.github.mamedovilkin.weather.network.model.Forecast
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.Weather

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