@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.ui.theme.cardBackgroundGradientEnd
import io.github.mamedovilkin.weather.ui.theme.cardBackgroundGradientStart
import io.github.mamedovilkin.weather.ui.theme.primary
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.mamedovilkin.weather.ui.screen.state.ErrorScreen
import io.github.mamedovilkin.weather.ui.screen.state.LoadingScreen
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.surface
import kotlin.math.ceil
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.convertToSymbol
import io.github.mamedovilkin.weather.ui.components.AdBanner
import io.github.mamedovilkin.weather.util.WeatherStat
import io.github.mamedovilkin.weather.util.getDate
import io.github.mamedovilkin.weather.util.getSeasonImageOfYear
import io.github.mamedovilkin.weather.util.getWeatherIcon
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    onNavigateToSearch: (String) -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { homeViewModel.fetchLocation() }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        homeViewModel.fetchUnit()
    }

    when (val screenState = uiState.homeScreenState) {
        HomeScreenState.Loading -> LoadingScreen()
        is HomeScreenState.Failure -> {
            ErrorScreen(
                e = screenState.e,
                onRetry = { homeViewModel.fetchUnit() }
            )
        }
        is HomeScreenState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .verticalScroll(rememberScrollState())
                        .animateContentSize(),
                ) {
                    CurrentWeather(
                        seasonImage = getSeasonImageOfYear(),
                        weather = screenState.currentWeather,
                        temperatureUnit = uiState.temperatureUnit,
                        onNavigateToSearch = onNavigateToSearch
                    )
                    WeatherStats(screenState.weatherStats)
                    AdBanner(1917863)
                    HourlyForecast(screenState.hourlyForecast)
                    DailyForecast(screenState.dailyForecast)
                    Spacer(modifier = Modifier.height(72.dp))
                }
                FloatingActionButton(
                    onClick = {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        homeViewModel.fetchCurrentLocation()
                    },
                    shape = CircleShape,
                    containerColor = cardBackgroundGradientEnd,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 80.dp, end = 8.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentWeather(
    @DrawableRes seasonImage: Int,
    weather: Weather,
    temperatureUnit: TemperatureUnit,
    onNavigateToSearch: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .height(500.dp)
    ) {
        Image(
            painter = painterResource(seasonImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(36.dp))
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(background, Color.Transparent),
                    startY = 100f
                )
            ))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Row {
                Text(
                    text = getDate(),
                    fontSize = 24.sp,
                    color = primary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.1F),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(vertical = 2.dp)
                    .padding(horizontal = 8.dp)
                    .clickable { onNavigateToSearch(weather.name) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = weather.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = primary
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(start = 4.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(getWeatherIcon(weather.icon)),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.size(88.dp),
                )
                Text(
                    text = stringResource(
                        R.string.temperature,
                        ceil(weather.temperature).toInt(),
                        temperatureUnit.convertToSymbol()
                    ),
                    fontSize = 88.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary,
                )
                Text(
                    text = stringResource(R.string.feels_like, weather.description, ceil(weather.feelsLike).toInt(), temperatureUnit.convertToSymbol()),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = primary
                )
            }
        }
    }
}

@Composable
fun WeatherStats(
    stats: List<WeatherStat>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(1F)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                cardBackgroundGradientStart,
                                cardBackgroundGradientEnd
                            )
                        )
                    )
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(stat.icon),
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(stat.title),
                    color = primary,
                    fontSize = 18.sp,
                )
                Text(
                    text = stat.stat,
                    color = primary,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
fun HourlyForecast(
    hourlyForecast: List<Weather>
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = surface
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(hourlyForecast) { weather ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = weather.datetime,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = primary
                    )
                    Image(
                        painter = painterResource(getWeatherIcon(weather.icon)),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "${ceil(weather.temperature).toInt()}°",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = primary
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecast(
    dailyForecast: List<Weather>
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = surface
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.daily_forecast),
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = primary,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 24.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            dailyForecast.forEach { weather ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = weather.datetime,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = primary,
                        modifier = Modifier.weight(1F)
                    )
                    Image(
                        painter = painterResource(getWeatherIcon(weather.icon)),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "${ceil(weather.maxTemperature).toInt()}°",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = primary,
                        modifier = Modifier.weight(1F)
                    )
                    Text(
                        text = "—",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        color = onPrimary
                    )
                    Text(
                        text = "${ceil(weather.minTemperature).toInt()}°",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = onPrimary,
                        modifier = Modifier.weight(1F)
                    )
                    Text(
                        text = weather.description,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = onPrimary,
                        modifier = Modifier.weight(1F)
                    )
                }
                if ((dailyForecast.size - 1) != dailyForecast.indexOf(weather)) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = onPrimary,
                        modifier = Modifier.alpha(0.5F)
                    )
                }
            }
        }
    }
}