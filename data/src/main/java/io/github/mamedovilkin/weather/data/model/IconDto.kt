@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class IconDto(
    val main: String,
    val description: String,
    val icon: String
)
