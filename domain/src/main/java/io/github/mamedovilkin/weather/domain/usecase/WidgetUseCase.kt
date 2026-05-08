package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository

class WidgetUseCase(
    private val dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository,
) {
    val location = dataStoreRepository.location
    val temperatureUnit = dataStoreRepository.temperatureUnit

    suspend fun getCurrentWeather(lat: Double, lon: Double, temperatureUnit: TemperatureUnit) = networkRepository.getCurrentWeather(lat, lon, temperatureUnit)
}