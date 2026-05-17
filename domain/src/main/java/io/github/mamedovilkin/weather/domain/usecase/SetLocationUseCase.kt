package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class SetLocationUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(name: String, lat: Double, lon: Double) = dataStoreRepository.setLocation(name, lat, lon)
}