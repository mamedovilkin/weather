package io.github.mamedovilkin.weather.datastore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DataStoreRepositoryTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataStoreRepository: DataStoreRepository

    @Before
    fun setUp() {
        dataStore = createTestDataStore(ApplicationProvider.getApplicationContext())
        dataStoreRepository = DataStoreRepositoryImpl(dataStore)
    }

    @Test
    fun testSetLocationAndGetLocation() = runBlocking {
        dataStoreRepository.setLocation(50.0, 50.0)

        val location = dataStoreRepository.location.first()

        assertEquals(50.0, location.first())
        assertEquals(50.0, location.last())
    }

    @Test
    fun testSetTemperatureUnitAndGetTemperatureUnit() = runBlocking {
        dataStoreRepository.setTemperatureUnit("metric")

        val temperatureUnit = dataStoreRepository.temperatureUnit.first()

        assertEquals("metric", temperatureUnit)
    }
}