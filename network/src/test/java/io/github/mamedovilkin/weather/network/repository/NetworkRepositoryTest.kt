package io.github.mamedovilkin.weather.network.repository

import io.github.mamedovilkin.weather.network.model.Forecast
import io.github.mamedovilkin.weather.network.model.Location
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.Weather
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking

class NetworkRepositoryTest : TestCase() {

    fun testGetCurrentWeatherSuccessReturnsWeather() = runBlocking {
        val networkRepository: NetworkRepository = NetworkRepositoryImpl(
            getSuccessHttpClientWeather()
        )

        val result = networkRepository.getCurrentWeather(50.0, 50.0, TemperatureUnit.METRIC)
        var weather: Weather? = null
        var e: Exception? = null

        result.onSuccess { weather = it }
        result.onFailure { e = it }

        assertTrue(weather!!.name!!.isNotEmpty())
        assertTrue(e == null)
    }

    fun testGetCurrentWeatherFailureReturnsException() = runBlocking {
        val networkRepository: NetworkRepository = NetworkRepositoryImpl(
            getFailureHttpClient()
        )

        val result = networkRepository.getCurrentWeather(50.0, 50.0, TemperatureUnit.METRIC)
        var weather: Weather? = null
        var e: Exception? = null

        result.onSuccess { weather = it }
        result.onFailure { e = it }

        assertTrue(weather == null)
        assertTrue(e != null)
    }

    fun testGetForecastSuccessReturnsForecast() = runBlocking {
        val networkRepository: NetworkRepository = NetworkRepositoryImpl(
            getSuccessHttpClientForecast()
        )

        val result = networkRepository.getForecast(50.0, 50.0, TemperatureUnit.METRIC)
        var forecast: Forecast? = null
        var e: Exception? = null

        result.onSuccess { forecast = it }
        result.onFailure { e = it }

        assertTrue(forecast!!.list.isNotEmpty())
        assertTrue(e == null)
    }

    fun testGetForecastFailureReturnsForecast() = runBlocking {
        val networkRepository: NetworkRepository = NetworkRepositoryImpl(
            getFailureHttpClient()
        )

        val result = networkRepository.getForecast(50.0, 50.0, TemperatureUnit.METRIC)
        var forecast: Forecast? = null
        var e: Exception? = null

        result.onSuccess { forecast = it }
        result.onFailure { e = it }

        assertTrue(forecast == null)
        assertTrue(e != null)
    }

    fun testSearchLocationSuccessReturnsListLocation() = runBlocking {
        val networkRepository: NetworkRepository = NetworkRepositoryImpl(
            getSuccessHttpClientListLocation()
        )

        val result = networkRepository.searchLocation("California")
        var locations: List<Location> = emptyList()
        var e: Exception? = null

        result.onSuccess { locations = it }
        result.onFailure { e = it }

        assertTrue(locations.isNotEmpty())
        assertTrue(e == null)
    }

    fun testSearchLocationFailureReturnsListLocation() = runBlocking {
        val networkRepository: NetworkRepository = NetworkRepositoryImpl(
            getFailureHttpClient()
        )

        val result = networkRepository.searchLocation("California")
        var locations: List<Location> = emptyList()
        var e: Exception? = null

        result.onSuccess { locations = it }
        result.onFailure { e = it }

        assertTrue(locations.isEmpty())
        assertTrue(e != null)
    }
}