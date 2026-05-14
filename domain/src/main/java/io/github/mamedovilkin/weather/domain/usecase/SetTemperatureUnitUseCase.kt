package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class SetTemperatureUnitUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(temperatureUnit: TemperatureUnit) = dataStoreRepository.setTemperatureUnit(temperatureUnit.name)
}