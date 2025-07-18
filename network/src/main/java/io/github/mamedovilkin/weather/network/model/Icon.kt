@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class Icon(
    val main: String,
    val description: String,
    val icon: String
)
