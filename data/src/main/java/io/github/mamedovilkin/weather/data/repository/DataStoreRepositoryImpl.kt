package io.github.mamedovilkin.weather.data.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.*
import io.github.mamedovilkin.weather.domain.model.LocationData
import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl (
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    private companion object {
        val NAME = stringPreferencesKey("name")
        val LAT = doublePreferencesKey("lat")
        val LON = doublePreferencesKey("lon")
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
        val PRESSURE_UNIT = stringPreferencesKey("pressure_unit")
    }

    override suspend fun setLocation(name: String, lat: Double, lon: Double) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
            preferences[LAT] = lat
            preferences[LON] = lon
        }
    }

    override val location: Flow<LocationData> = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                LocationData(
                    preferences[NAME] ?: "",
                    preferences[LAT] ?: 0.0,
                    preferences[LON] ?: 0.0,
                )
            }

    override suspend fun setTemperatureUnit(temperatureUnit: String) {
        dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT] = temperatureUnit
        }
    }

    override val temperatureUnit: Flow<String>
        get() = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[TEMPERATURE_UNIT] ?: TemperatureUnit.CELSIUS.name
            }

    override suspend fun setWindSpeedUnit(windSpeedUnit: String) {
        dataStore.edit { preferences ->
            preferences[WIND_SPEED_UNIT] = windSpeedUnit
        }
    }

    override val windSpeedUnit: Flow<String>
        get() = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[WIND_SPEED_UNIT] ?: WindSpeedUnit.MS.name
            }

    override suspend fun setPressureUnit(pressureUnit: String) {
        dataStore.edit { preferences ->
            preferences[PRESSURE_UNIT] = pressureUnit
        }
    }

    override val pressureUnit: Flow<String>
        get() = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[PRESSURE_UNIT] ?: PressureUnit.MMHG.name
            }
}