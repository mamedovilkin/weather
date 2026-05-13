package io.github.mamedovilkin.weather.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setLocation(lat: Double, lon: Double)
    val location: Flow<List<Double>>
    suspend fun setTemperatureUnit(temperatureUnit: String)
    val temperatureUnit: Flow<String>
    suspend fun setWindSpeedUnit(windSpeedUnit: String)
    val windSpeedUnit: Flow<String>
    suspend fun setPressureUnit(pressureUnit: String)
    val pressureUnit: Flow<String>
}