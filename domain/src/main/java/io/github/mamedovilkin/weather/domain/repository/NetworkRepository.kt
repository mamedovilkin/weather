package io.github.mamedovilkin.weather.domain.repository

import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.util.Result

interface NetworkRepository {

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit,
    ): Result<Weather>

    suspend fun searchLocation(
        query: String,
    ): Result<List<Location>>
}