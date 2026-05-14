package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class GetWindSpeedUnitUseCase(
    dataStoreRepository: DataStoreRepository
) {
    val windSpeedUnit = dataStoreRepository.windSpeedUnit
}