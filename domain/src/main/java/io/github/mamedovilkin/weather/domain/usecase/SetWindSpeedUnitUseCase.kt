package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class SetWindSpeedUnitUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(windSpeedUnit: WindSpeedUnit) = dataStoreRepository.setWindSpeedUnit(windSpeedUnit.name)
}