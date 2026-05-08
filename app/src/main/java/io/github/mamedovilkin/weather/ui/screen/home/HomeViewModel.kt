@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.convertToPressureUnit
import io.github.mamedovilkin.weather.domain.model.convertToTemperatureUnit
import io.github.mamedovilkin.weather.domain.usecase.HomeUseCase
import io.github.mamedovilkin.weather.util.WeatherStat
import io.github.mamedovilkin.weather.util.getDate
import io.github.mamedovilkin.weather.util.getTime
import io.github.mamedovilkin.weather.util.isTodayForecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

sealed interface HomeScreenState {
    data class Failure(val e: Exception): HomeScreenState
    data class Success(
        val currentWeather: Weather,
        val weatherStats: List<WeatherStat>,
        val hourlyForecast: List<Weather>,
        val dailyForecast: List<Weather>,
    ): HomeScreenState
    data object Loading: HomeScreenState
}

data class HomeUiState(
    val homeScreenState: HomeScreenState = HomeScreenState.Loading,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.METRIC,
    val pressureUnit: PressureUnit = PressureUnit.MMHG,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)

class HomeViewModel(
    private val application: Application,
    private val homeUseCase: HomeUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun fetchUnits() = viewModelScope.launch {
        combine(homeUseCase.temperatureUnit, homeUseCase.pressureUnit) { (temperatureUnit, pressureUnit) ->
            temperatureUnit to pressureUnit
        }.catch {
            setTemperatureUnit(TemperatureUnit.METRIC)
            setPressureUnit(PressureUnit.MMHG)
        }.collect {
            setTemperatureUnit(it.first.convertToTemperatureUnit())
            setPressureUnit(it.second.convertToPressureUnit())
            fetchLocation()
        }
    }

    fun fetchLocation() = viewModelScope.launch {
        homeUseCase.location
            .catch { e ->
                setFailureHomeScreenState(Exception(e))
            }
            .collect {
                val lat = it.first()
                val lon = it.last()

                if (lat == 0.0 && lon == 0.0) {
                    fetchCurrentLocation()
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(lat = lat, lon = lon)
                    }
                }

                fetchWeather()
            }
    }

    fun fetchCurrentLocation() {
        homeUseCase.getCurrentLocation { location ->
            viewModelScope.launch {
                homeUseCase.setLocation(
                    lat = location?.latitude ?: 0.0,
                    lon = location?.longitude ?: 0.0
                )
            }
        }
    }

    private fun fetchWeather() = viewModelScope.launch {
        homeUseCase
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
    ) = viewModelScope.launch {
        homeUseCase
            .getForecast(
                _uiState.value.lat,
                _uiState.value.lon,
                _uiState.value.temperatureUnit,
            )
            .onSuccess { data ->
                _uiState.update { currentState ->
                    currentState.copy(
                        homeScreenState = HomeScreenState.Success(
                            currentWeather = weather,
                            weatherStats = makeWeatherStats(weather),
                            hourlyForecast = data.forecast
                                .filter { isTodayForecast(it.datetime) }
                                .map { it.copy(datetime = getTime(application, it.datetime)) },
                            dailyForecast = data.forecast
                                .filter { !isTodayForecast(it.datetime) }
                                .map { it.copy(datetime = getDate(it.datetime)) }
                                .distinctBy { it.datetime },
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

    private fun setPressureUnit(pressureUnit: PressureUnit) {
        _uiState.update { currentState ->
            currentState.copy(
                pressureUnit = pressureUnit
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
                    application.getString(R.string.ms, weather.windSpeed.toString())
                } else {
                    application.getString(R.string.mh, weather.windSpeed.toString())
                },
            ),
            WeatherStat(
                icon = R.drawable.ic_pressure,
                title = R.string.pressure,
                stat = if (_uiState.value.pressureUnit == PressureUnit.MB) {
                    application.getString(R.string.mb, weather.pressure.toString())
                } else {
                    application.getString(R.string.mmhg, weather.mmHG.toString())
                },
            ),
            WeatherStat(
                icon = R.drawable.ic_humidity,
                title = R.string.humidity,
                stat = "${weather.humidity}%",
            )
        )
    }
}