@file:Suppress("PropertyName")
@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.data.model

import io.github.mamedovilkin.weather.data.entity.WeatherEntity
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val dt: Long,
    val main: MainDto,
    val name: String? = null,
    val sys: SysDto,
    val wind: WindDto,
    val weather: List<IconDto>,
    val dt_txt: String? = null
)

fun WeatherDto.toEntityWeather(): WeatherEntity {
    return WeatherEntity(
        id = 0,
        name = name.toString(),
        icon = weather.first().icon,
        description = weather.first().description,
        temperature = main.temp,
        maxTemperature = main.temp_max,
        minTemperature = main.temp_min,
        feelsLike = main.feels_like,
        windSpeed = wind.speed,
        humidity = main.humidity,
        pressure = main.pressure,
        datetime = dt_txt.toString()
    )
}