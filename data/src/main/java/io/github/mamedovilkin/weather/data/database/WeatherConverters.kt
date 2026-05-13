package io.github.mamedovilkin.weather.data.database

import androidx.room.TypeConverter
import io.github.mamedovilkin.weather.data.entity.DailyEntity
import io.github.mamedovilkin.weather.data.entity.HourlyEntity
import kotlinx.serialization.json.Json

class WeatherConverters {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromHourly(value: HourlyEntity): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toHourly(value: String): HourlyEntity {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromDaily(value: DailyEntity): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toDaily(value: String): DailyEntity {
        return json.decodeFromString(value)
    }
}