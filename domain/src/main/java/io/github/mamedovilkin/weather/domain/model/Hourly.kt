package io.github.mamedovilkin.weather.domain.model

data class Hourly(
    val times: List<String>,
    val temperatures: List<Double>,
    val weatherCodes: List<Int>,
)