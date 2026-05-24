package io.github.mamedovilkin.weather.data.mapper

import io.github.mamedovilkin.weather.domain.entity.DailyEntity
import io.github.mamedovilkin.weather.domain.entity.HourlyEntity
import io.github.mamedovilkin.weather.domain.entity.WeatherEntity
import io.github.mamedovilkin.weather.data.model.WeatherDto
import io.github.mamedovilkin.weather.domain.model.Daily
import io.github.mamedovilkin.weather.domain.model.Hourly
import io.github.mamedovilkin.weather.domain.model.Weather

fun WeatherDto.toEntityWeather(name: String?) =
    WeatherEntity(
        name = name,
        latitude = latitude,
        longitude = longitude,
        temperature = current.temperature_2m,
        feelsLike = current.apparent_temperature,
        weatherCode = current.weather_code,
        wind = current.wind_speed_10m,
        humidity = current.relative_humidity_2m,
        pressure = current.surface_pressure,
        isDay = current.is_day,
        hourly = HourlyEntity(
            times = hourly.time,
            temperatures = hourly.temperature_2m,
            weatherCodes = hourly.weather_code,
            isDays = hourly.is_day,
        ),
        daily = DailyEntity(
            times = daily.time,
            maxTemperatures = daily.temperature_2m_max,
            minTemperatures = daily.temperature_2m_min,
            weatherCodes = daily.weather_code,
            sunrises = daily.sunrise,
            sunsets = daily.sunset,
            uvIndexes = daily.uv_index_max,
        )
    )

fun WeatherEntity.toDomainWeather() =
    Weather(
        name = name,
        latitude = latitude,
        longitude = longitude,
        temperature = temperature,
        feelsLike = feelsLike,
        weatherCode = weatherCode,
        wind = wind,
        humidity = humidity,
        pressure = pressure,
        isDay = isDay,
        hourly = Hourly(
            times = hourly.times,
            temperatures = hourly.temperatures,
            weatherCodes = hourly.weatherCodes,
            isDays = hourly.isDays
        ),
        daily = Daily(
            times = daily.times,
            maxTemperatures = daily.maxTemperatures,
            minTemperatures = daily.minTemperatures,
            weatherCodes = daily.weatherCodes,
            sunrises = daily.sunrises,
            sunsets = daily.sunsets,
            uvIndexes = daily.uvIndexes
        )
    )