package io.github.mamedovilkin.weather.mock

import io.github.mamedovilkin.weather.datastore.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDataStoreRepository : DataStoreRepository {

    private var locationFlow = MutableStateFlow(listOf(50.0, 50.0))
    private var temperatureUnitFlow = MutableStateFlow("IMPERIAL")

    override suspend fun setLocation(lat: Double, lon: Double) {
        locationFlow.value = listOf(lat, lon)
    }

    override val location: Flow<List<Double>>
        get() = locationFlow

    override suspend fun setTemperatureUnit(temperatureUnit: String) {
        temperatureUnitFlow.value = temperatureUnit
    }

    override val temperatureUnit: Flow<String>
        get() = temperatureUnitFlow
}