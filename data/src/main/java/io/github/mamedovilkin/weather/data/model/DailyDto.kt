@file:Suppress("PropertyName")

package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyDto(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val weather_code: List<Int>,
    val sunrise: List<String>,
    val sunset: List<String>,
    val uv_index_max: List<Double>,
)
