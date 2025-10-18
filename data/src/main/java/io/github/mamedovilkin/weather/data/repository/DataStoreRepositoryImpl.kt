package io.github.mamedovilkin.weather.data.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.*
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl (
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    private companion object {
        val LAT = doublePreferencesKey("lat")
        val LON = doublePreferencesKey("lon")
        val UNIT = stringPreferencesKey("units")
    }

    override suspend fun setLocation(lat: Double, lon: Double) {
        dataStore.edit { preferences ->
            preferences[LAT] = lat
            preferences[LON] = lon
        }
    }

    override val location: Flow<List<Double>> = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                listOf(
                    preferences[LAT] ?: 0.0,
                    preferences[LON] ?: 0.0,
                )
            }

    override suspend fun setTemperatureUnit(temperatureUnit: String) {
        dataStore.edit { preferences ->
            preferences[UNIT] = temperatureUnit
        }
    }

    override val temperatureUnit: Flow<String>
        get() = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[UNIT] ?: "METRIC"
            }
}