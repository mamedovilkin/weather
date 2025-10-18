package io.github.mamedovilkin.weather.domain.model

data class Location(
    val name: String,
    val ru: String? = null,
    val en: String? = null,
    val country: String,
    val state: String? = null,
    val lat: Double,
    val lon: Double
)