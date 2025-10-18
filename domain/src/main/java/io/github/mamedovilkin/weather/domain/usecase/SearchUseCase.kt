package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository

class SearchUseCase(
    private val dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository
) {
    val location = dataStoreRepository.location

    suspend fun setLocation(lat: Double, lon: Double) = dataStoreRepository.setLocation(lat, lon)

    suspend fun searchLocation(query: String) = networkRepository.searchLocation(query)
}