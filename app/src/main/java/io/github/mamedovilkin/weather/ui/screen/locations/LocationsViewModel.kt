package io.github.mamedovilkin.weather.ui.screen.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.domain.entity.LocationEntity
import io.github.mamedovilkin.weather.domain.model.LocationData
import io.github.mamedovilkin.weather.domain.usecase.DeleteLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetLocationsUseCase
import io.github.mamedovilkin.weather.domain.usecase.SetLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LocationsScreenState {
    data class Failure(val e: Exception): LocationsScreenState
    data class Success(
        val locations: List<LocationEntity>,
        val defaultLocation: LocationData
    ): LocationsScreenState
    data object Loading: LocationsScreenState
    data object Init: LocationsScreenState
}

data class LocationsUiState(
    val locationsScreenState: LocationsScreenState = LocationsScreenState.Init,
)

class LocationsViewModel(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val setLocationUseCase: SetLocationUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        fetchLocations()
    }

    fun fetchLocations() = viewModelScope.launch {
        _uiState.update { currentState ->
            currentState.copy(
                locationsScreenState = LocationsScreenState.Loading
            )
        }

        combine(
            getLocationUseCase.location,
            getLocationsUseCase.locations
        ) { defaultLocation, locations ->
            defaultLocation to locations
        }.catch { e ->
            _uiState.update { currentState ->
                currentState.copy(
                    locationsScreenState = LocationsScreenState.Failure(Exception(e))
                )
            }
        }.collect { (defaultLocation, locations) ->
            _uiState.update { currentState ->
                currentState.copy(
                    locationsScreenState = LocationsScreenState.Success(locations, defaultLocation)
                )
            }
        }
    }

    fun setLocation(location: LocationEntity) = viewModelScope.launch {
        setLocationUseCase(location.name, location.lat, location.lon)
    }

    fun deleteLocation(location: LocationEntity) = viewModelScope.launch {
        deleteLocationUseCase(location)
    }
}