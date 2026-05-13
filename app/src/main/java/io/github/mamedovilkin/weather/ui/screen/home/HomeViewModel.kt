@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.model.convertToPressureUnit
import io.github.mamedovilkin.weather.domain.model.convertToTemperatureUnit
import io.github.mamedovilkin.weather.domain.model.convertToWindSpeedUnit
import io.github.mamedovilkin.weather.domain.usecase.HomeUseCase
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
        val weather: Weather
    ): HomeScreenState
    data object Loading: HomeScreenState
}

data class HomeUiState(
    val homeScreenState: HomeScreenState = HomeScreenState.Loading,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.MS,
    val pressureUnit: PressureUnit = PressureUnit.MMHG,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

class HomeViewModel(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun fetchUnits() = viewModelScope.launch {
        combine(homeUseCase.temperatureUnit, homeUseCase.windSpeedUnit, homeUseCase.pressureUnit) { (temperatureUnit, windSpeedUnit, pressureUnit) ->
            Triple(temperatureUnit, windSpeedUnit, pressureUnit)
        }.catch {
            setUnit(TemperatureUnit.CELSIUS)
            setUnit(WindSpeedUnit.MS)
            setUnit(PressureUnit.MMHG)
        }.collect {
            setUnit(it.first.convertToTemperatureUnit())
            setUnit(it.second.convertToWindSpeedUnit())
            setUnit(it.third.convertToPressureUnit())
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

                fetchLocation()
            }
        }
    }

    private fun fetchWeather() = viewModelScope.launch {
        homeUseCase
            .getCurrentWeather(
                _uiState.value.lat,
                _uiState.value.lon,
                _uiState.value.temperatureUnit,
                _uiState.value.windSpeedUnit
            )
            .onSuccess { weather ->
                _uiState.update { currentState ->
                    currentState.copy(
                        homeScreenState = HomeScreenState.Success(weather = weather)
                    )
                }
            }
            .onFailure { e ->
                setFailureHomeScreenState(e)
            }
    }

    private fun setUnit(temperatureUnit: TemperatureUnit) {
        _uiState.update { currentState ->
            currentState.copy(
                temperatureUnit = temperatureUnit
            )
        }
    }

    private fun setUnit(windSpeedUnit: WindSpeedUnit) {
        _uiState.update { currentState ->
            currentState.copy(
                windSpeedUnit = windSpeedUnit
            )
        }
    }

    private fun setUnit(pressureUnit: PressureUnit) {
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
}