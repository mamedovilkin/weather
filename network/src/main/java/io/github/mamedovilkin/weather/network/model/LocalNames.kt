@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class LocalNames(
    var en: String? = null,
    var ru: String? = null
)
