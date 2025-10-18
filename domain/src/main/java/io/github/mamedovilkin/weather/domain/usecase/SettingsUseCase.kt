package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class SettingsUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    val temperatureUnit = dataStoreRepository.temperatureUnit

    suspend fun setTemperatureUnit(temperatureUnit: TemperatureUnit) = dataStoreRepository.setTemperatureUnit(temperatureUnit.name)
}