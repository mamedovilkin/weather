@file:Suppress("PropertyName")

package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentDto(
    val temperature_2m: Double,
    val apparent_temperature: Double,
    val weather_code: Int,
    val wind_speed_10m: Double,
    val relative_humidity_2m: Int,
    val surface_pressure: Double,
    val is_day: Int,
)