@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.data.model

import io.github.mamedovilkin.weather.data.entity.ForecastEntity
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class ForecastDto(
    val list: List<WeatherDto>
)

fun ForecastDto.toEntityForecast(): ForecastEntity {
    return ForecastEntity(
        id = 0,
        forecast = list.map { it.toEntityWeather() }
    )
}