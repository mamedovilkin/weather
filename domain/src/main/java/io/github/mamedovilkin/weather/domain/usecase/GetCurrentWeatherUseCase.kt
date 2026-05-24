package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.domain.util.Result

class GetCurrentWeatherUseCase(
    private val networkRepository: NetworkRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, temperatureUnit: TemperatureUnit, windSpeedUnit: WindSpeedUnit, forecastDays: Int = 16): Result<Weather> = networkRepository.getWeather(lat, lon, temperatureUnit, windSpeedUnit, forecastDays)
}