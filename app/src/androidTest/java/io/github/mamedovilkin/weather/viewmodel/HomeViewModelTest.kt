package io.github.mamedovilkin.weather.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import io.github.mamedovilkin.weather.mock.FakeDataStoreRepository
import io.github.mamedovilkin.weather.mock.FakeFailureNetworkRepository
import io.github.mamedovilkin.weather.mock.FakeLocationService
import io.github.mamedovilkin.weather.mock.FakeSuccessNetworkRepository
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.ui.screen.home.HomeScreenState
import io.github.mamedovilkin.weather.ui.screen.home.HomeViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class HomeViewModelTest {

    private val application = ApplicationProvider.getApplicationContext<Application>()
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
    fun testFetchUnit_setTemperatureUnitToUiState()
    = runTest {
        val homeViewModel = HomeViewModel(
            application,
            FakeDataStoreRepository(),
            FakeSuccessNetworkRepository(),
            FakeLocationService(),
            testDispatcher
        )

        homeViewModel.fetchUnit()

        advanceUntilIdle()

        val homeUiState = homeViewModel.uiState.value

        assertTrue(homeUiState.homeScreenState is HomeScreenState.Success)
        assertEquals(TemperatureUnit.IMPERIAL, homeUiState.temperatureUnit)
        assertEquals(50.0, homeUiState.lat)
        assertEquals(50.0, homeUiState.lon)
    }

    @Test
    fun testFetchLocation_setSuccessHomeScreenState()
    = runTest {
        val homeViewModel = HomeViewModel(
            application,
            FakeDataStoreRepository(),
            FakeSuccessNetworkRepository(),
            FakeLocationService(),
            testDispatcher
        )

        homeViewModel.fetchLocation()

        advanceUntilIdle()

        val homeUiState = homeViewModel.uiState.value

        assertTrue(homeUiState.homeScreenState is HomeScreenState.Success)
    }

    @Test
    fun testFetchLocation_setFailureHomeScreenState()
            = runTest {
        val homeViewModel = HomeViewModel(
            application,
            FakeDataStoreRepository(),
            FakeFailureNetworkRepository(),
            FakeLocationService(),
            testDispatcher
        )

        homeViewModel.fetchLocation()

        advanceUntilIdle()

        val homeUiState = homeViewModel.uiState.value

        assertTrue(homeUiState.homeScreenState is HomeScreenState.Failure)
    }
}