package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchDto(
    val results: List<LocationDto>
)
