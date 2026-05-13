package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository

class WidgetUseCase(
    dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository,
) {
    val location = dataStoreRepository.location

    val temperatureUnit = dataStoreRepository.temperatureUnit

    val windSpeedUnit = dataStoreRepository.windSpeedUnit

    suspend fun getCurrentWeather(lat: Double, lon: Double, temperatureUnit: TemperatureUnit, windSpeedUnit: WindSpeedUnit) = networkRepository.getWeather(lat, lon, temperatureUnit, windSpeedUnit)
}