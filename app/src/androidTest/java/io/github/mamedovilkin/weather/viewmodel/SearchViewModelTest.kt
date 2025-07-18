package io.github.mamedovilkin.weather.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import io.github.mamedovilkin.weather.mock.FakeDataStoreRepository
import io.github.mamedovilkin.weather.mock.FakeFailureNetworkRepository
import io.github.mamedovilkin.weather.mock.FakeSuccessNetworkRepository
import io.github.mamedovilkin.weather.ui.screen.search.SearchScreenState
import io.github.mamedovilkin.weather.ui.screen.search.SearchViewModel
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
class SearchViewModelTest {

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
    fun testPopularLocation_popularLocationsIsNotEmpty() = runTest {
        val searchViewModel = SearchViewModel(
            application,
            FakeDataStoreRepository(),
            FakeSuccessNetworkRepository(),
            testDispatcher
        )

        assertTrue(searchViewModel.popularLocations.isNotEmpty())
    }

    @Test
    fun testSetLocation() = runTest {
        val dataStoreRepository = FakeDataStoreRepository()

        val searchViewModel = SearchViewModel(
            application,
            dataStoreRepository,
            FakeSuccessNetworkRepository(),
            testDispatcher
        )

        searchViewModel.setLocation(100.0, 100.0)

        advanceUntilIdle()

        val lat = dataStoreRepository.location.first().first()
        val lon = dataStoreRepository.location.first().last()

        assertEquals(100.0, lat)
        assertEquals(100.0, lon)
    }

    @Test
    fun testSetSearchQuery() = runTest {
        val searchViewModel = SearchViewModel(
            application,
            FakeDataStoreRepository(),
            FakeSuccessNetworkRepository(),
            testDispatcher
        )

        searchViewModel.setSearchQuery("Test")

        val searchUiState = searchViewModel.uiState

        assertEquals("Test", searchUiState.value.searchQuery)
    }

    @Test
    fun testFetchLocation() = runTest {
        val searchViewModel = SearchViewModel(
            application,
            FakeDataStoreRepository(),
            FakeSuccessNetworkRepository(),
            testDispatcher
        )

        searchViewModel.fetchLocation()

        advanceUntilIdle()

        val searchUiState = searchViewModel.uiState

        assertEquals(50.0, searchUiState.value.lat)
        assertEquals(50.0, searchUiState.value.lon)
    }

    @Test
    fun testFetchLocationsWithEmptyQuery() = runTest {
        val searchViewModel = SearchViewModel(
            application,
            FakeDataStoreRepository(),
            FakeSuccessNetworkRepository(),
            testDispatcher
        )

        searchViewModel.fetchLocations("")

        advanceUntilIdle()

        val searchUiState = searchViewModel.uiState

        assertTrue(searchUiState.value.searchScreenState is SearchScreenState.Init)
    }

    @Test
    fun testFetchLocationsWithSuccessNetwork() = runTest {
        val searchViewModel = SearchViewModel(
            application,
            FakeDataStoreRepository(),
            FakeSuccessNetworkRepository(),
            testDispatcher
        )

        searchViewModel.fetchLocations("Test")

        advanceUntilIdle()

        val searchUiState = searchViewModel.uiState

        assertTrue(searchUiState.value.searchScreenState is SearchScreenState.Success)
    }

    @Test
    fun testFetchLocationsWithFailureNetwork() = runTest {
        val searchViewModel = SearchViewModel(
            application,
            FakeDataStoreRepository(),
            FakeFailureNetworkRepository(),
            testDispatcher
        )

        searchViewModel.fetchLocations("Test")

        advanceUntilIdle()

        val searchUiState = searchViewModel.uiState

        assertTrue(searchUiState.value.searchScreenState is SearchScreenState.Failure)
    }
}