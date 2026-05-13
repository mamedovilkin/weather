package io.github.mamedovilkin.weather.domain.usecase

import android.location.Location
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.domain.service.LocationService
import io.github.mamedovilkin.weather.domain.util.Result

class HomeUseCase(
    private val dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository,
    private val locationService: LocationService
) {
    val location = dataStoreRepository.location

    val temperatureUnit = dataStoreRepository.temperatureUnit

    val windSpeedUnit = dataStoreRepository.windSpeedUnit

    val pressureUnit = dataStoreRepository.pressureUnit

    fun getCurrentLocation(callback: (Location?) -> Unit) = locationService.getCurrentLocation(callback)

    suspend fun setLocation(lat: Double, lon: Double) = dataStoreRepository.setLocation(lat, lon)

    suspend fun getCurrentWeather(lat: Double, lon: Double, temperatureUnit: TemperatureUnit, windSpeedUnit: WindSpeedUnit): Result<Weather> = networkRepository.getWeather(lat, lon, temperatureUnit, windSpeedUnit)
}