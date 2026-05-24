package io.github.mamedovilkin.weather.ui.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.ui.screen.state.ErrorScreen
import io.github.mamedovilkin.weather.ui.screen.state.LoadingScreen
import io.github.mamedovilkin.weather.ui.screen.state.NoResultsScreen
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.primary
import org.koin.androidx.compose.koinViewModel
import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.ui.components.SearchBar

@Composable
fun SearchScreen(
    city: String? = null,
    searchViewModel: SearchViewModel = koinViewModel()
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val locations by searchViewModel.locations.collectAsStateWithLifecycle(emptyList())

    LaunchedEffect(Unit) {
        if (city != null && city != "{city}") {
            searchViewModel.setSearchQuery(city)
            searchViewModel.fetchLocations(city)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        SearchBar(
            searchQuery = uiState.searchQuery,
            setSearchQuery = { searchViewModel.setSearchQuery(it) },
            onSearch = { searchViewModel.fetchLocations(it) },
            onClose = { searchViewModel.reset() }
        )
        when (val screenState = uiState.searchScreenState) {
            SearchScreenState.Init -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    NoResultsScreen(
                        message = stringResource(R.string.search_summary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F)
                    )
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
            SearchScreenState.NoResults -> {
                NoResultsScreen(
                    message = stringResource(R.string.no_results, uiState.searchQuery),
                    modifier = Modifier.fillMaxSize()
                )
            }
            SearchScreenState.Loading -> LoadingScreen()
            is SearchScreenState.Failure -> {
                ErrorScreen(
                    e = screenState.e,
                    onRetry = { searchViewModel.fetchLocations(uiState.searchQuery) }
                )
            }
            is SearchScreenState.Success -> {
                LocationsList(
                    locations = locations.map { it.lat to it.lon }.toSet(),
                    found = screenState.locations,
                    onSetLocation = { name, lat, lon ->
                        searchViewModel.setLocation(name, lat, lon)
                    }
                )
            }
        }
    }
}

@Composable
fun LocationsList(
    locations: Set<Pair<Double, Double>>,
    found: List<Location>,
    onSetLocation: (name: String, lat: Double, lon: Double) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        itemsIndexed(found) { index, location ->
            val isSaved = remember(location, locations) {
                (location.latitude to location.longitude) in locations
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(36.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1F)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = location.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primary,
                    )
                    if (location.country != null) {
                        Text(
                            text = location.country!!,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = onPrimary,
                        )
                    }
                }
                Button(
                    onClick = {
                        onSetLocation(
                            location.name,
                            location.latitude,
                            location.longitude
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (isSaved)
                                primary.copy(alpha = 0.1F)
                            else
                                primary
                    )
                ) {
                    if (isSaved) {
                        Icon(
                            painter = painterResource(R.drawable.ic_done),
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = null,
                            tint = background,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            if (index != found.size - 1) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = onPrimary,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .alpha(0.5F)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}