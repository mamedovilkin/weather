package io.github.mamedovilkin.weather.viewmodel

import io.github.mamedovilkin.weather.mock.FakeDataStoreRepository
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.convertToUnit
import io.github.mamedovilkin.weather.ui.screen.settings.SettingsViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSetExpanded() = runTest {
        val settingsViewModel = SettingsViewModel(
            FakeDataStoreRepository(),
            testDispatcher
        )

        settingsViewModel.setExpanded(true)

        val settingsUiState = settingsViewModel.uiState

        assertTrue(settingsUiState.value.expanded)
    }

    @Test
    fun testSetUnit() = runTest {
        val dataStoreRepository = FakeDataStoreRepository()
        val settingsViewModel = SettingsViewModel(
            dataStoreRepository,
            testDispatcher
        )

        settingsViewModel.setUnit(TemperatureUnit.METRIC)

        advanceUntilIdle()

        val result = dataStoreRepository.temperatureUnit.first().convertToUnit()

        assertEquals(TemperatureUnit.METRIC, result)
    }

    @Test
    fun testFetchUnit() = runTest {
        val settingsViewModel = SettingsViewModel(
            FakeDataStoreRepository(),
            testDispatcher
        )

        settingsViewModel.fetchUnit()

        advanceUntilIdle()

        val settingsUiState = settingsViewModel.uiState

        assertEquals(TemperatureUnit.IMPERIAL, settingsUiState.value.temperatureUnit)
    }

}