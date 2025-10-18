@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.domain.usecase.SearchUseCase
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
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)

class SearchViewModel(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun fetchLocation() = viewModelScope.launch {
        searchUseCase.location
            .catch { e ->
                setFailureSearchScreenState(Exception(e))
            }
            .collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        lat = it.first(),
                        lon = it.last()
                    )
                }
            }
    }

    fun setLocation(lat: Double, lon: Double) = viewModelScope.launch {
        searchUseCase.setLocation(lat, lon)
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

        searchUseCase
            .searchLocation(searchQuery)
            .onSuccess { data ->
                _uiState.update { currentState ->
                    if (data.isNotEmpty()) {
                        currentState.copy(
                            searchScreenState = SearchScreenState.Success(
                                locations = data.map { it.copy(state = it.state ?: it.name) }
                            )
                        )
                    } else {
                        currentState.copy(
                            searchScreenState = SearchScreenState.NoResults
                        )
                    }
                }
            }
            .onFailure { e ->
                setFailureSearchScreenState(e)
            }
    }

    private fun setFailureSearchScreenState(e: Exception) {
        _uiState.update { currentState ->
            currentState.copy(
                searchScreenState = SearchScreenState.Failure(e = Exception(e))
            )
        }
    }
}