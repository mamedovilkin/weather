@file:OptIn(InternalSerializationApi::class)
@file:Suppress("PropertyName")

package io.github.mamedovilkin.weather.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val name: String,
    val local_names: LocalNames? = null,
    val country: String,
    val state: String? = null,
    val lat: Double,
    val lon: Double,
)