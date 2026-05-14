package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class SetPressureUnitUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(pressureUnit: PressureUnit) = dataStoreRepository.setPressureUnit(pressureUnit.name)
}