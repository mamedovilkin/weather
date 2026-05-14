package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class GetLocationUseCase(
    dataStoreRepository: DataStoreRepository,
) {
    val location = dataStoreRepository.location
}