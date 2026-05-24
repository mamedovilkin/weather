package io.github.mamedovilkin.weather.domain.model

data class Weather(
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val feelsLike: Double,
    val weatherCode: Int,
    val wind: Double,
    val humidity: Int,
    val pressure: Double,
    val isDay: Int,
    val hourly: Hourly,
    val daily: Daily
) {
    val mmHG = pressure * 0.750
}