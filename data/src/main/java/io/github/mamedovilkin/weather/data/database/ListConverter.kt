package io.github.mamedovilkin.weather.data.database

import androidx.room.TypeConverter
import io.github.mamedovilkin.weather.data.entity.WeatherEntity
import kotlinx.serialization.json.Json

class ListConverter {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
        explicitNulls = false
    }

    @TypeConverter fun fromWeatherList(list: List<WeatherEntity>?): String = json.encodeToString(list ?: emptyList())

    @TypeConverter fun toWeatherList(value: String): List<WeatherEntity> = json.decodeFromString(value)
}