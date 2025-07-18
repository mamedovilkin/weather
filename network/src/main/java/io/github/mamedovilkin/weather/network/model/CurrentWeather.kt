package io.github.mamedovilkin.weather.network.model

data class CurrentWeather(
    val icon: String,
    val date: String,
    val weekDay: String,
    val city: String,
    val countryState: String,
    val temp: Int,
    val feelsLikeTemp: Int,
    val description: String
)