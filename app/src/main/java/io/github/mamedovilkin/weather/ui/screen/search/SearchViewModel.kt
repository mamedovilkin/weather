@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.domain.usecase.GetLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.SearchLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.SetLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

sealed interface SearchScreenState {
    data class Failure(val e: Exception): SearchScreenState
    data class Success(
        val locations: List<Location>
    ): SearchScreenState
    data object Loading: SearchScreenState
    data object NoResults: SearchScreenState
    data object Init: SearchScreenState
}

data class SearchUiState(
    val searchScreenState: SearchScreenState = SearchScreenState.Init,
    val searchQuery: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)

class SearchViewModel(
    private val getLocationUseCase: GetLocationUseCase,
    private val setLocationUseCase: SetLocationUseCase,
    private val searchLocationUseCase: SearchLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun fetchLocation() = viewModelScope.launch {
        getLocationUseCase.location
            .catch { e ->
                setFailureSearchScreenState(Exception(e))
            }
            .collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        name = it.name,
                        lat = it.lat,
                        lon = it.lon
                    )
                }
            }
    }

    fun setLocation(name: String, lat: Double, lon: Double) = viewModelScope.launch {
        setLocationUseCase(name, lat, lon)
    }

    fun setSearchQuery(searchQuery: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = searchQuery
            )
        }
    }

    fun fetchLocations(searchQuery: String) = viewModelScope.launch {
        if (searchQuery.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    searchScreenState = SearchScreenState.Init,
                )
            }

            return@launch
        }

        _uiState.update { currentState ->
            currentState.copy(
                searchScreenState = SearchScreenState.Loading,
            )
        }

        searchLocationUseCase(searchQuery)
            .onSuccess { locations, _ ->
                _uiState.update { currentState ->
                    if (locations.isNotEmpty()) {
                        currentState.copy(
                            searchScreenState = SearchScreenState.Success(
                                locations = locations
                            )
                        )
                    } else {
                        currentState.copy(
                            searchScreenState = SearchScreenState.NoResults
                        )
                    }
                }
            }
            .onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        searchScreenState = SearchScreenState.NoResults
                    )
                }
            }
    }

    fun reset() {
        _uiState.value = SearchUiState()
    }

    private fun setFailureSearchScreenState(e: Exception) {
        _uiState.update { currentState ->
            currentState.copy(
                searchScreenState = SearchScreenState.Failure(e = Exception(e))
            )
        }
    }
}