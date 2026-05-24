@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.domain.model.LocationData
import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.model.convertToPressureUnit
import io.github.mamedovilkin.weather.domain.model.convertToTemperatureUnit
import io.github.mamedovilkin.weather.domain.model.convertToWindSpeedUnit
import io.github.mamedovilkin.weather.domain.usecase.GetCurrentLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetCurrentWeatherUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetPressureUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetTemperatureUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetWindSpeedUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.SetLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

sealed interface HomeScreenState {
    data class Failure(val e: Exception): HomeScreenState
    data class Success(
        val weather: Weather,
        val isOffline: Boolean = false,
    ): HomeScreenState
    data object Loading: HomeScreenState
}

data class HomeUiState(
    val homeScreenState: HomeScreenState = HomeScreenState.Loading,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.MS,
    val pressureUnit: PressureUnit = PressureUnit.MMHG,
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

class HomeViewModel(
    private val getTemperatureUnitUseCase: GetTemperatureUnitUseCase,
    private val getWindSpeedUnitUseCase: GetWindSpeedUnitUseCase,
    private val getPressureUnitUseCase: GetPressureUnitUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val setLocationUseCase: SetLocationUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun fetchData() = viewModelScope.launch {
        val values = combine(
            listOf(
                getTemperatureUnitUseCase.temperatureUnit,
                getWindSpeedUnitUseCase.windSpeedUnit,
                getPressureUnitUseCase.pressureUnit,
                getLocationUseCase.location,
            )
        ) { values -> values }
            .catch { e ->
                setFailureHomeScreenState(Exception(e))
            }.first()

        _uiState.update { currentState ->
            currentState.copy(
                temperatureUnit = values[0].toString().convertToTemperatureUnit(),
                windSpeedUnit = values[1].toString().convertToWindSpeedUnit(),
                pressureUnit = values[2].toString().convertToPressureUnit(),
                name = (values[3] as LocationData).name,
                lat = (values[3] as LocationData).lat,
                lon = (values[3] as LocationData).lon
            )
        }

        fetchWeather()
    }

    private fun fetchWeather() = viewModelScope.launch {
        getCurrentWeatherUseCase(
            _uiState.value.lat,
            _uiState.value.lon,
            _uiState.value.temperatureUnit,
            _uiState.value.windSpeedUnit
        )
            .onSuccess { weather, isOffline ->
                _uiState.update { currentState ->
                    currentState.copy(
                        homeScreenState = HomeScreenState.Success(
                            weather = weather,
                            isOffline = isOffline
                        ),
                    )
                }
            }
            .onFailure { e ->
                setFailureHomeScreenState(e)
            }
    }

    fun fetchCurrentLocation() {
        getCurrentLocationUseCase { location ->
            viewModelScope.launch {
                setLocationUseCase(
                    name = _uiState.value.name,
                    lat = location?.latitude ?: 0.0,
                    lon = location?.longitude ?: 0.0
                )

                fetchData()
            }
        }
    }

    fun retry() {
        _uiState.update { currentState ->
            currentState.copy(
                homeScreenState = HomeScreenState.Loading
            )
        }

        fetchWeather()
    }

    private fun setFailureHomeScreenState(e: Exception) {
        _uiState.update { currentState ->
            currentState.copy(
                homeScreenState = HomeScreenState.Failure(e = Exception(e))
            )
        }
    }
}