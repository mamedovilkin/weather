package io.github.mamedovilkin.weather.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mamedovilkin.weather.datastore.repository.DataStoreRepository
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.convertToUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val expanded: Boolean = false,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.METRIC
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setExpanded(expanded: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(expanded)
        }
    }

    fun setUnit(temperatureUnit: TemperatureUnit) = viewModelScope.launch(dispatcher) {
        dataStoreRepository.setTemperatureUnit(temperatureUnit.name)
    }

    fun fetchUnit() = viewModelScope.launch(dispatcher) {
        dataStoreRepository.temperatureUnit
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