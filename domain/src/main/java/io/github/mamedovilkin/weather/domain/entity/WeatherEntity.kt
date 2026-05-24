package io.github.mamedovilkin.weather.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "weather")
@Serializable
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val name: String? = null,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val feelsLike: Double,
    val weatherCode: Int,
    val wind: Double,
    val humidity: Int,
    val pressure: Double,
    val isDay: Int,
    val hourly: HourlyEntity,
    val daily: DailyEntity
)