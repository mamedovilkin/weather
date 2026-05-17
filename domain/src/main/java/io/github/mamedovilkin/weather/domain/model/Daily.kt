package io.github.mamedovilkin.weather.domain.model

data class Daily(
    val times: List<String>,
    val maxTemperatures: List<Double>,
    val minTemperatures: List<Double>,
    val weatherCodes: List<Int>,
    val sunrises: List<String>,
    val sunsets: List<String>,
    val uvIndexes: List<Double>,
)