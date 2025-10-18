@file:Suppress("PropertyName")
@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class MainDto(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int,
    val pressure: Int,
)