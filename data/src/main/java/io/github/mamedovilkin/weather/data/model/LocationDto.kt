@file:OptIn(InternalSerializationApi::class)
@file:Suppress("PropertyName")

package io.github.mamedovilkin.weather.data.model

import io.github.mamedovilkin.weather.domain.model.Location
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val name: String,
    val local_names: LocalNamesDto? = null,
    val country: String,
    val state: String? = null,
    val lat: Double,
    val lon: Double,
)

fun LocationDto.toDomainLocation(): Location {
    return Location(
        name = name,
        ru = local_names?.ru,
        en = local_names?.en,
        country = country,
        state = state,
        lat = lat,
        lon = lon
    )
}