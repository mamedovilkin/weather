@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.data.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class LocalNamesDto(
    var en: String? = null,
    var ru: String? = null
)