package io.github.mamedovilkin.weather.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.mamedovilkin.weather.domain.model.Forecast
import kotlinx.serialization.Serializable

@Entity(tableName = "forecast")
@Serializable
data class ForecastEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val forecast: List<WeatherEntity>
)

fun ForecastEntity.toDomainForecast(): Forecast {
    return Forecast(
        forecast = forecast.map { it.toDomainWeather() }
    )
}