package io.github.mamedovilkin.weather.datastore.repository

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
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

fun createTestDataStore(context: Context): DataStore<Preferences> {
    val dataStoreFile = File(context.filesDir, "test.preferences_pb")

    return if (dataStoreFile.exists()) {
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(dataStoreFile.name)
        }
    } else {
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = {
                File(context.filesDir, "test.preferences_pb").also {
                    it.deleteOnExit()
                }
            }
        )
    }
}