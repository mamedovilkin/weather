package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class GetPressureUnitUseCase(
    dataStoreRepository: DataStoreRepository
) {
    val pressureUnit = dataStoreRepository.pressureUnit
}