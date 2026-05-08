package io.github.mamedovilkin.weather.domain.usecase

import android.location.Location
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.domain.service.LocationService

class HomeUseCase(
    private val dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository,
    private val locationService: LocationService
) {

    val temperatureUnit = dataStoreRepository.temperatureUnit
    val pressureUnit = dataStoreRepository.pressureUnit
    val location = dataStoreRepository.location

    fun getCurrentLocation(callback: (Location?) -> Unit) = locationService.getCurrentLocation(callback)

    suspend fun setLocation(lat: Double, lon: Double) = dataStoreRepository.setLocation(lat, lon)

    suspend fun getCurrentWeather(lat: Double, lon: Double, temperatureUnit: TemperatureUnit) = networkRepository.getCurrentWeather(lat, lon, temperatureUnit)

    suspend fun getForecast(lat: Double, lon: Double, temperatureUnit: TemperatureUnit) = networkRepository.getForecast(lat, lon, temperatureUnit)
}