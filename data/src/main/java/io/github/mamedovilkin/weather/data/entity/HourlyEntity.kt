package io.github.mamedovilkin.weather.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class HourlyEntity(
    val times: List<String>,
    val temperatures: List<Double>,
    val weatherCodes: List<Int>,
)