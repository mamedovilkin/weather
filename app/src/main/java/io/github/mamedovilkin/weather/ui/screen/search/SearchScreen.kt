package io.github.mamedovilkin.weather.ui.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.network.model.Location
import io.github.mamedovilkin.weather.ui.screen.state.ErrorScreen
import io.github.mamedovilkin.weather.ui.screen.state.LoadingScreen
import io.github.mamedovilkin.weather.ui.screen.state.NoResultsScreen
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.primary
import io.github.mamedovilkin.weather.util.PopularLocation
import io.github.mamedovilkin.weather.util.textWithEllipsis
import java.util.Locale

@Composable
fun SearchScreen(
    city: String? = null,
    onNavigateToHome: () -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (city != null && city != "{city}") {
            searchViewModel.setSearchQuery(city)
            searchViewModel.fetchLocations(city)
        }

        searchViewModel.fetchLocation()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        SearchBar(
            searchQuery = uiState.searchQuery,
            setSearchQuery = { searchViewModel.setSearchQuery(it) },
            onSearch = { searchViewModel.fetchLocations(it) }
        )
        when (val screenState = uiState.searchScreenState) {
            SearchScreenState.Init -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.popular_locations),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primary,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    )
                    PopularLocations(
                        popularLocations = searchViewModel.popularLocations,
                        onLocation = {
                            searchViewModel.setLocation(
                                lat = it.lat,
                                lon = it.lon,
                            )
                            onNavigateToHome()
                        }
                    )
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
                    onRetry = { searchViewModel.fetchLocation() }
                )
            }
            is SearchScreenState.Success -> {
                Text(
                    text = stringResource(R.string.results_found, screenState.locations.size),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                LocationsList(
                    locations = screenState.locations,
                    selectedLat = uiState.lat,
                    selectedLon = uiState.lon,
                    onSetLocation = { lat, lon ->
                        searchViewModel.setLocation(lat, lon)
                    }
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    setSearchQuery: (String) -> Unit,
    onSearch: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searchQuery,
        onValueChange = { setSearchQuery(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions (
            onSearch = {
                onSearch(searchQuery)
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = background
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = primary,
            unfocusedContainerColor = primary,
            disabledContainerColor = primary,
            focusedTextColor = background,
            unfocusedTextColor = background,
            cursorColor = background,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("search_bar")
    )
}

@Composable
fun PopularLocations(
    popularLocations: List<PopularLocation>,
    onLocation: (PopularLocation) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        items(popularLocations) { location ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = background
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .width(120.dp)
                    .clickable { onLocation(location) }
                    .testTag("card")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val painter = rememberAsyncImagePainter(location.image)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black),
                                startY = 0.2f
                            )
                        )
                    ) {}
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = location.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationsList(
    locations: List<Location>,
    selectedLat: Double,
    selectedLon: Double,
    onSetLocation: (lat: Double, lon: Double) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(locations) { location ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = if (Locale.getDefault().language == "ru") location.local_names?.ru?.textWithEllipsis() ?: location.name.textWithEllipsis() else location.local_names?.en?.textWithEllipsis() ?: location.name.textWithEllipsis(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primary,
                    )
                    Text(
                        text = "${location.state}, ${location.country}".textWithEllipsis(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onPrimary,
                    )
                }
                Button(
                    onClick = { onSetLocation(location.lat, location.lon) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (
                            location.lat == selectedLat && location.lon == selectedLon
                        ) primary.copy(alpha = 0.1F) else primary
                    )
                ) {
                    if (location.lat == selectedLat && location.lon == selectedLon) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = primary
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.set_location),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            maxLines = 2,
                            color = background
                        )

                    }
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = onPrimary,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .alpha(0.5F)
            )
        }
    }
}