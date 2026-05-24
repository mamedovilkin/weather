package io.github.mamedovilkin.weather.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class DailyEntity(
    val times: List<String>,
    val maxTemperatures: List<Double>,
    val minTemperatures: List<Double>,
    val weatherCodes: List<Int>,
    val sunrises: List<String>,
    val sunsets: List<String>,
    val uvIndexes: List<Double>,
)
