package io.github.mamedovilkin.weather.datastore.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setLocation(lat: Double, lon: Double)
    val location: Flow<List<Double>>
    suspend fun setTemperatureUnit(temperatureUnit: String)
    val temperatureUnit: Flow<String>
}