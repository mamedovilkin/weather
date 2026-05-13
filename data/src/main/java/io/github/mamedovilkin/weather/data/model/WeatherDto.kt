package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val latitude: Double,
    val longitude: Double,
    val current: CurrentDto,
    val hourly: HourlyDto,
    val daily: DailyDto,
)
