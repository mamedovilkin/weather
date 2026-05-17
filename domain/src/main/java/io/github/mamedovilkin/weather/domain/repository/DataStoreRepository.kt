package io.github.mamedovilkin.weather.domain.repository

import io.github.mamedovilkin.weather.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setLocation(name: String, lat: Double, lon: Double)
    val location: Flow<LocationData>
    suspend fun setTemperatureUnit(temperatureUnit: String)
    val temperatureUnit: Flow<String>
    suspend fun setWindSpeedUnit(windSpeedUnit: String)
    val windSpeedUnit: Flow<String>
    suspend fun setPressureUnit(pressureUnit: String)
    val pressureUnit: Flow<String>
}