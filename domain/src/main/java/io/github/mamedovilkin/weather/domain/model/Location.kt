package io.github.mamedovilkin.weather.domain.model

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
)