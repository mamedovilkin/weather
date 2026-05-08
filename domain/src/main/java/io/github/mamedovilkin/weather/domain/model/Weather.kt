package io.github.mamedovilkin.weather.domain.model

data class Weather(
    val name: String,
    val icon: String,
    val description: String,
    val temperature: Double,
    val maxTemperature: Double,
    val minTemperature: Double,
    val feelsLike: Double,
    val windSpeed: Double,
    val humidity: Int,
    val pressure: Int,
    val datetime: String
) {
    val mmHG = (pressure * 0.750) * 100 / 100
}