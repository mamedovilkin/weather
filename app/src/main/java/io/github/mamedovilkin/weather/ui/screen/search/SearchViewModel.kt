@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.datastore.repository.DataStoreRepository
import io.github.mamedovilkin.weather.network.model.Location
import io.github.mamedovilkin.weather.network.repository.NetworkRepository
import io.github.mamedovilkin.weather.util.PopularLocation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

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

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val networkRepository: NetworkRepository,
    private val dispatcher: CoroutineDispatcher
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    val popularLocations: List<PopularLocation> = listOf(
        PopularLocation(
            image = R.drawable.new_york,
            name = application.getString(R.string.new_york),
            lat = 40.7127281,
            lon = -74.0060152
        ),
        PopularLocation(
            image = R.drawable.london,
            name = application.getString(R.string.london),
            lat = 51.5073219,
            lon = -0.1276474
        ),
        PopularLocation(
            image = R.drawable.paris,
            name = application.getString(R.string.paris),
            lat = 48.8588897,
            lon = 2.32004102172008
        ),
        PopularLocation(
            image = R.drawable.warsaw,
            name = application.getString(R.string.warsaw),
            lat = 52.2319581,
            lon = 21.0067249
        ),
        PopularLocation(
            image = R.drawable.rome,
            name = application.getString(R.string.rome),
            lat = 41.8933203,
            lon = 12.4829321
        )
    )

    fun fetchLocation() = viewModelScope.launch(dispatcher) {
        dataStoreRepository.location
            .catch { e ->
                setFailureSearchScreenState(Exception(e))
            }
            .collect {
                val lat = it.first()
                val lon = it.last()

                _uiState.update { currentState ->
                    currentState.copy(
                        lat = lat,
                        lon = lon
                    )
                }
            }
    }

    fun setLocation(lat: Double, lon: Double) = viewModelScope.launch(dispatcher) {
        dataStoreRepository.setLocation(lat, lon)
    }

    fun setSearchQuery(searchQuery: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = searchQuery
            )
        }
    }

    fun fetchLocations(searchQuery: String) = viewModelScope.launch(dispatcher) {
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

        networkRepository
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