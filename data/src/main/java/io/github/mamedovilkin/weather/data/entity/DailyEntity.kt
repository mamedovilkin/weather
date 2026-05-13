package io.github.mamedovilkin.weather.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class DailyEntity(
    val times: List<String>,
    val maxTemperatures: List<Double>,
    val minTemperatures: List<Double>,
    val weatherCodes: List<Int>,
)
