package io.github.mamedovilkin.weather.ui.screen.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.domain.entity.LocationEntity
import io.github.mamedovilkin.weather.ui.screen.state.ErrorScreen
import io.github.mamedovilkin.weather.ui.screen.state.LoadingScreen
import io.github.mamedovilkin.weather.ui.screen.state.NoResultsScreen
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.primary
import io.github.mamedovilkin.weather.ui.theme.surface
import org.koin.androidx.compose.koinViewModel
import kotlin.math.ceil
import kotlin.math.round

@Composable
fun LocationsScreen(
    locationsViewModel: LocationsViewModel = koinViewModel(),
    onBack:() -> Unit
) {
    val uiState by locationsViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = stringResource(R.string.locations),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = primary
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            when (val screenState = uiState.locationsScreenState) {
                LocationsScreenState.Loading -> item {
                    LoadingScreen()
                }

                is LocationsScreenState.Failure -> item {
                    ErrorScreen(
                        e = screenState.e,
                        onRetry = { locationsViewModel.fetchLocations() }
                    )
                }

                is LocationsScreenState.Success -> {
                    stickyHeader {
                        Text(
                            text = stringResource(R.string.default_),
                            color = onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(background)
                                .padding(8.dp)
                        )
                    }

                    item {
                        LocationItem(
                            location = LocationEntity(
                                id = 0,
                                name = screenState.defaultLocation.name,
                                lat = screenState.defaultLocation.lat,
                                lon = screenState.defaultLocation.lon
                            )
                        )
                    }

                    stickyHeader {
                        Text(
                            text = stringResource(R.string.other_locations),
                            color = onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(background)
                                .padding(8.dp)
                        )
                    }

                    items(screenState.locations) { location ->
                        if (ceil(screenState.defaultLocation.lat) != ceil(location.lat) && ceil(screenState.defaultLocation.lon) != ceil(location.lon)) {
                            LocationItem(
                                location = location,
                                isDefault = false,
                                onSetLocation = { locationsViewModel.setLocation(it) },
                                onDelete = { locationsViewModel.deleteLocation(it) }
                            )
                        }
                    }
                }

                else -> item {
                    Column(modifier = Modifier.fillMaxSize()) {
                        NoResultsScreen(
                            message = "There are no locations.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1F)
                        )
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

    }
}

@Composable
fun LocationItem(
    location: LocationEntity,
    isDefault: Boolean = true,
    onSetLocation: (LocationEntity) -> Unit = {},
    onDelete: (LocationEntity) -> Unit = {},
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = surface
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1F)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = location.name.ifEmpty { stringResource(R.string.unknown) },
                        color = primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${round(location.lat * 100) / 100}°",
                        color = onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "|",
                        color = onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${round(location.lon * 100) / 100}°",
                        color = onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            if (!isDefault) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1F)
                ) {
                    Button(
                        onClick = { onSetLocation(location) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primary
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.set),
                            color = background,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { onDelete(location) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = primary
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                            tint = background
                        )
                    }
                }
            }
        }
    }
}