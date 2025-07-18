@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.datastore.repository.DataStoreRepository
import io.github.mamedovilkin.weather.network.model.CurrentWeather
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.Weather
import io.github.mamedovilkin.weather.network.model.convertToUnit
import io.github.mamedovilkin.weather.network.model.toCurrentWeather
import io.github.mamedovilkin.weather.network.repository.NetworkRepository
import io.github.mamedovilkin.weather.service.LocationService
import io.github.mamedovilkin.weather.util.WeatherStat
import io.github.mamedovilkin.weather.util.getDate
import io.github.mamedovilkin.weather.util.getTime
import io.github.mamedovilkin.weather.util.isTodayForecast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

sealed interface HomeScreenState {
    data class Failure(val e: Exception): HomeScreenState
    data class Success(
        val currentWeather: CurrentWeather,
        val weatherStats: List<WeatherStat>,
        val hourlyForecast: List<Weather>,
        val dailyForecast: List<Weather>,
    ): HomeScreenState
    data object Loading: HomeScreenState
}

data class HomeUiState(
    val homeScreenState: HomeScreenState = HomeScreenState.Loading,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.METRIC,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository,
    private val locationService: LocationService,
    private val dispatcher: CoroutineDispatcher
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun fetchUnit() = viewModelScope.launch(dispatcher) {
        dataStoreRepository.temperatureUnit
            .catch {
                setTemperatureUnit(TemperatureUnit.METRIC)
            }
            .collect {
                setTemperatureUnit(it.convertToUnit())
                fetchLocation()
            }
    }

    fun fetchLocation() = viewModelScope.launch(dispatcher) {
        dataStoreRepository.location
            .catch { e ->
                setFailureHomeScreenState(Exception(e))
            }
            .collect {
                val lat = it.first()
                val lon = it.last()

                if (lat == 0.0 && lon == 0.0) {
                    locationService.getCurrentLocation { location ->
                        viewModelScope.launch(dispatcher) {
                            dataStoreRepository.setLocation(
                                lat = location?.latitude ?: 0.0,
                                lon = location?.longitude ?: 0.0
                            )
                        }
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(lat = lat, lon = lon)
                    }
                }

                fetchWeather()
            }
    }

    private fun fetchWeather() = viewModelScope.launch(dispatcher) {
        networkRepository
            .getCurrentWeather(
                _uiState.value.lat,
                _uiState.value.lon,
                _uiState.value.temperatureUnit,
            )
            .onSuccess { data ->
                fetchForecast(data)
            }
            .onFailure { e ->
                setFailureHomeScreenState(e)
            }
    }

    private fun fetchForecast(
        weather: Weather
    ) = viewModelScope.launch(dispatcher) {

        networkRepository
            .getForecast(
                _uiState.value.lat,
                _uiState.value.lon,
                _uiState.value.temperatureUnit,
            )
            .onSuccess { data ->
                _uiState.update { currentState ->
                    currentState.copy(
                        homeScreenState = HomeScreenState.Success(
                            currentWeather = weather.toCurrentWeather(),
                            weatherStats = makeWeatherStats(weather),
                            hourlyForecast = data.list
                                .filter { isTodayForecast(it.dt_txt.toString()) }
                                .map { it.copy(dt_txt = getTime(application, it.dt_txt.toString())) },
                            dailyForecast = data.list
                                .filter { !isTodayForecast(it.dt_txt.toString()) }
                                .map { it.copy(dt_txt = getDate(it.dt_txt.toString())) }
                                .distinctBy { it.dt_txt },
                        )
                    )
                }
            }
            .onFailure { e ->
                setFailureHomeScreenState(e)
            }
    }

    private fun setTemperatureUnit(temperatureUnit: TemperatureUnit) {
        _uiState.update { currentState ->
            currentState.copy(
                temperatureUnit = temperatureUnit
            )
        }
    }

    private fun setFailureHomeScreenState(e: Exception) {
        _uiState.update { currentState ->
            currentState.copy(
                homeScreenState = HomeScreenState.Failure(e = Exception(e))
            )
        }
    }

    private fun makeWeatherStats(weather: Weather): List<WeatherStat> {
        return listOf(
            WeatherStat(
                icon = R.drawable.ic_wind,
                title = R.string.wind,
                stat = if (_uiState.value.temperatureUnit == TemperatureUnit.METRIC) {
                    application.getString(R.string.ms, weather.wind.speed.toString())
                } else {
                    application.getString(R.string.mh, weather.wind.speed.toString())
                },
            ),
            WeatherStat(
                icon = R.drawable.ic_pressure,
                title = R.string.pressure,
                stat = application.getString(R.string.mb, weather.main.pressure.toString()),
            ),
            WeatherStat(
                icon = R.drawable.ic_humidity,
                title = R.string.humidity,
                stat = "${weather.main.humidity}%",
            )
        )
    }
}