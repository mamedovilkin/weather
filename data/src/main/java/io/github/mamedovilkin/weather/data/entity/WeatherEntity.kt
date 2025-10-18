package io.github.mamedovilkin.weather.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.mamedovilkin.weather.domain.model.Weather
import kotlinx.serialization.Serializable

@Entity(tableName = "weather")
@Serializable
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val icon: String,
    val description: String,
    val temperature: Double,
    val maxTemperature: Double,
    val minTemperature: Double,
    val feelsLike: Double,
    val windSpeed: Double,
    val humidity: Int,
    val pressure: Int,
    val datetime: String
)

fun WeatherEntity.toDomainWeather(): Weather {
    return Weather(
        name = name,
        icon = icon,
        description = description,
        temperature = temperature,
        maxTemperature = maxTemperature,
        minTemperature = minTemperature,
        feelsLike = feelsLike,
        windSpeed = windSpeed,
        humidity = humidity,
        pressure = pressure,
        datetime = datetime
    )
}