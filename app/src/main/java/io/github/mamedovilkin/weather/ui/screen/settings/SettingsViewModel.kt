package io.github.mamedovilkin.weather.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.model.convertToPressureUnit
import io.github.mamedovilkin.weather.domain.model.convertToTemperatureUnit
import io.github.mamedovilkin.weather.domain.model.convertToWindSpeedUnit
import io.github.mamedovilkin.weather.domain.usecase.SettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val expandedTemperatureUnit: Boolean = false,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val expandedWindSpeedUnit: Boolean = false,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.MS,
    val expandedPressureUnit: Boolean = false,
    val pressureUnit: PressureUnit = PressureUnit.MMHG,
    val showMassage: Boolean = false,
)

class SettingsViewModel(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setExpandedTemperatureUnit(expandedTemperatureUnit: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(expandedTemperatureUnit = expandedTemperatureUnit)
        }
    }

    fun setExpandedWindSpeedUnit(expandedWindSpeedUnit: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(expandedWindSpeedUnit = expandedWindSpeedUnit)
        }
    }


    fun setExpandedPressureUnit(expandedPressureUnit: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(expandedPressureUnit = expandedPressureUnit)
        }
    }

    fun setUnit(temperatureUnit: TemperatureUnit) = viewModelScope.launch {
        settingsUseCase.setTemperatureUnit(temperatureUnit)
    }

    fun setUnit(windSpeedUnit: WindSpeedUnit) = viewModelScope.launch {
        settingsUseCase.setWindSpeedUnit(windSpeedUnit)
    }

    fun setUnit(pressureUnit: PressureUnit) = viewModelScope.launch {
        settingsUseCase.setPressureUnit(pressureUnit)
    }

    fun fetchUnits() = viewModelScope.launch {
        combine(
            settingsUseCase.temperatureUnit,
            settingsUseCase.windSpeedUnit,
            settingsUseCase.pressureUnit
        ) { temp, windSpeed, pressure ->
            Triple(temp, windSpeed, pressure)
        }
            .catch {
                setTemperatureUnit(TemperatureUnit.CELSIUS)
                setWindSpeedUnit(WindSpeedUnit.MS)
                setPressureUnit(PressureUnit.MMHG)
            }
            .collect { (temp, windSpeed, pressure) ->
                setTemperatureUnit(temp.convertToTemperatureUnit())
                setWindSpeedUnit(windSpeed.convertToWindSpeedUnit())
                setPressureUnit(pressure.convertToPressureUnit())
            }
    }

    private fun setTemperatureUnit(temperatureUnit: TemperatureUnit) {
        _uiState.update { currentState ->
            currentState.copy(temperatureUnit = temperatureUnit)
        }
    }

    private fun setWindSpeedUnit(windSpeedUnit: WindSpeedUnit) {
        _uiState.update { currentState ->
            currentState.copy(windSpeedUnit = windSpeedUnit)
        }
    }

    private fun setPressureUnit(pressureUnit: PressureUnit) {
        _uiState.update { currentState ->
            currentState.copy(pressureUnit = pressureUnit)
        }
    }

    fun setShowMassage(showMassage: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(showMassage = showMassage)
        }
    }
}