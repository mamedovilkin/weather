@file:Suppress("PropertyName")

package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HourlyDto(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>,
)
