package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class GetTemperatureUnitUseCase(
    dataStoreRepository: DataStoreRepository
) {
    val temperatureUnit = dataStoreRepository.temperatureUnit
}