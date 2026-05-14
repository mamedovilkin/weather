package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.repository.NetworkRepository

class SearchLocationUseCase(
    private val networkRepository: NetworkRepository
) {
    suspend operator fun invoke(query: String) = networkRepository.searchLocation(query)
}