package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country: String? = null,
)