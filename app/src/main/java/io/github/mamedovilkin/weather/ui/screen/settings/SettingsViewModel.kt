package io.github.mamedovilkin.weather.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.convertToUnit
import io.github.mamedovilkin.weather.domain.usecase.SettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val expanded: Boolean = false,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.METRIC
)

class SettingsViewModel(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setExpanded(expanded: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(expanded = expanded)
        }
    }

    fun setUnit(temperatureUnit: TemperatureUnit) = viewModelScope.launch {
        settingsUseCase.setTemperatureUnit(temperatureUnit)
    }

    fun fetchUnit() = viewModelScope.launch {
        settingsUseCase.temperatureUnit
            .catch {
                setTemperatureUnit(TemperatureUnit.METRIC)
            }
            .collect {
                setTemperatureUnit(it.convertToUnit())
            }
    }

    private fun setTemperatureUnit(temperatureUnit: TemperatureUnit) {
        _uiState.update { currentState ->
            currentState.copy(temperatureUnit = temperatureUnit)
        }
    }
}