@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.screen.home

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.ui.screen.state.ErrorScreen
import io.github.mamedovilkin.weather.ui.screen.state.LoadingScreen
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.surface
import kotlin.math.ceil
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.model.convertToSymbol
import io.github.mamedovilkin.weather.ui.components.AdBanner
import io.github.mamedovilkin.weather.ui.model.WeatherStat
import io.github.mamedovilkin.weather.util.getDate
import io.github.mamedovilkin.weather.util.getLocalTime
import io.github.mamedovilkin.weather.util.getSeasonImageOfYear
import io.github.mamedovilkin.weather.util.getTime
import io.github.mamedovilkin.weather.util.getUVIndexDescription
import io.github.mamedovilkin.weather.util.getWeatherDescription
import io.github.mamedovilkin.weather.util.getWeatherIcon
import io.github.mamedovilkin.weather.util.hasLocationPermission
import kotlinx.serialization.InternalSerializationApi
import org.koin.androidx.compose.koinViewModel
import java.time.LocalTime
import kotlin.collections.chunked

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    onNavigateToSearch: (String) -> Unit
) {
    val context = LocalContext.current
    val permissionMessage = stringResource(R.string.location_permission_message)
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                homeViewModel.fetchCurrentLocation()
            } else {
                Toast.makeText(context, permissionMessage, Toast.LENGTH_LONG).show()
            }
        }
    )
    val state = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (!hasLocationPermission(context)) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    SideEffect {
        homeViewModel.fetchData()
    }

    when (val screenState = uiState.homeScreenState) {
        HomeScreenState.Loading -> LoadingScreen()
        is HomeScreenState.Failure -> {
            ErrorScreen(
                e = screenState.e,
                onRetry = { homeViewModel.retry() }
            )
        }
        is HomeScreenState.Success -> {
            val times = remember(screenState.weather.hourly.times) {
                screenState.weather.hourly.times.chunked(24)
            }

            val temperatures = remember(screenState.weather.hourly.temperatures) {
                screenState.weather.hourly.temperatures.chunked(24)
            }

            val weatherCodes = remember(screenState.weather.hourly.weatherCodes) {
                screenState.weather.hourly.weatherCodes.chunked(24)
            }

            val scrollToIndex = remember(screenState.weather.hourly.times) {  screenState.weather.hourly.times.indexOfFirst { !LocalTime.now().isAfter(getLocalTime(it)) } }

            LaunchedEffect(scrollToIndex) {
                if (scrollToIndex >= 0) {
                    state.scrollToItem(scrollToIndex)
                }
            }

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
                        weather = screenState.weather,
                        name = uiState.name,
                        temperatureUnit = uiState.temperatureUnit,
                        onNavigateToSearch = onNavigateToSearch,
                    )
                    AnimatedVisibility(screenState.isOffline) {
                        WarningItem()
                    }
                    AdBanner(
                        context = context
                    )
                    WeatherStats(
                        weatherStats = listOf(
                            WeatherStat(
                                icon = R.drawable.ic_wind,
                                title = R.string.wind,
                                stat = stringResource(
                                    when (uiState.windSpeedUnit) {
                                        WindSpeedUnit.KMH -> R.string.kmh
                                        WindSpeedUnit.MPH -> R.string.mph
                                        else -> R.string.ms
                                    }, screenState.weather.wind
                                ),
                            ),
                            WeatherStat(
                                icon = R.drawable.ic_pressure,
                                title = R.string.pressure,
                                stat = if (uiState.pressureUnit == PressureUnit.MB) {
                                    stringResource(R.string.mb, screenState.weather.pressure)
                                } else {
                                    stringResource(R.string.mmhg, screenState.weather.mmHG)
                                }
                            ),
                            WeatherStat(
                                icon = R.drawable.ic_humidity,
                                title = R.string.humidity,
                                stat = "${screenState.weather.humidity}%",
                            )
                        )
                    )
                    WeatherStats(
                        weatherStats = listOf(
                            WeatherStat(
                                icon = R.drawable.ic_sunrise,
                                title = R.string.sunrise,
                                stat = getTime(context, screenState.weather.daily.sunrises.first()),
                            ),
                            WeatherStat(
                                icon = R.drawable.ic_sunset,
                                title = R.string.sunset,
                                stat = getTime(context, screenState.weather.daily.sunsets.first())
                            ),
                            WeatherStat(
                                icon = R.drawable.ic_sun,
                                title = R.string.uv_index,
                                stat = "${screenState.weather.daily.uvIndexes.first()} (${stringResource(getUVIndexDescription(screenState.weather.daily.uvIndexes.first())).lowercase()})"
                            )
                        )
                    )
                    HourlyForecast(
                        state = state,
                        context = context,
                        times = times,
                        temperatures = temperatures,
                        weatherCodes = weatherCodes,
                    )
                    DailyForecast(
                        context = context,
                        weather = screenState.weather,
                        times = times,
                        temperatures = temperatures,
                        weatherCodes = weatherCodes,
                    )
                    Spacer(modifier = Modifier.height(72.dp))
                }
                FloatingActionButton(
                    onClick = {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
    name: String?,
    temperatureUnit: TemperatureUnit,
    onNavigateToSearch: (String) -> Unit,
) {
    val unknown = stringResource(R.string.unknown)

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .height(500.dp)
    ) {
        AsyncImage(
            model = seasonImage,
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
                    .clickable { onNavigateToSearch(weather.name ?: name ?: unknown) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = weather.name ?: name ?: unknown,
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
                AsyncImage(
                    model = getWeatherIcon(weather.weatherCode),
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
                    text = stringResource(R.string.feels_like, stringResource(getWeatherDescription(weather.weatherCode)).lowercase(), ceil(weather.feelsLike).toInt(), temperatureUnit.convertToSymbol()),
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
fun WarningItem() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = surface
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = R.drawable.ic_error,
                contentDescription = null,
                colorFilter = ColorFilter.tint(primary),
                modifier = Modifier
                    .padding(16.dp)
                    .size(36.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_internet_connection),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = primary,
                )
                Text(
                    text = stringResource(R.string.warning_message),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = onPrimary,
                )
            }
        }
    }
}

@Composable
fun WeatherStats(
    weatherStats: List<WeatherStat>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weatherStats.forEach { weatherStat ->
            WeatherStatItem(
                weatherStat = weatherStat,
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
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun WeatherStatItem(
    weatherStat: WeatherStat,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(weatherStat.icon),
            contentDescription = null,
            tint = primary.copy(alpha = 0.2F),
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = stringResource(weatherStat.title),
                color = primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = weatherStat.stat,
                color = primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HourlyForecast(
    state: LazyListState,
    context: Context,
    times: List<List<String>>,
    temperatures: List<List<Double>>,
    weatherCodes: List<List<Int>>
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
            state = state,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(times[0].size) { i ->
                val isAfter = LocalTime.now().isAfter(getLocalTime(times[0][i]))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = getTime(context, times[0][i]),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = if (isAfter) onPrimary else primary
                    )
                    AsyncImage(
                        model = getWeatherIcon(weatherCodes[0][i]),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp)
                    )
                    Text(
                        text = "${ceil(temperatures[0][i]).toInt()}°",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = if (isAfter) onPrimary else primary
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecast(
    context: Context,
    weather: Weather,
    times: List<List<String>>,
    temperatures: List<List<Double>>,
    weatherCodes: List<List<Int>>
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
            repeat(weather.daily.times.size) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = getDate(weather.daily.times[index]),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = primary,
                        modifier = Modifier.weight(1F)
                    )
                    AsyncImage(
                        model = getWeatherIcon(weather.daily.weatherCodes[index]),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                    )
                    Text(
                        text = "${ceil(weather.daily.minTemperatures[index]).toInt()}°",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = onPrimary,
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
                        text = "${ceil(weather.daily.maxTemperatures[index]).toInt()}°",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = primary,
                        modifier = Modifier.weight(1F)
                    )
                    Text(
                        text = stringResource(getWeatherDescription(weather.daily.weatherCodes[index])).lowercase(),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        autoSize = TextAutoSize.StepBased(maxFontSize = 18.sp),
                        color = onPrimary,
                        modifier = Modifier.weight(1F)
                    )
                }
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(times[index].size) { i ->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = getTime(context, times[index][i]),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = primary
                            )
                            AsyncImage(
                                model = getWeatherIcon(weatherCodes[index][i]),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(24.dp)
                            )
                            Text(
                                text = "${ceil(temperatures[index][i]).toInt()}°",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = primary
                            )
                        }
                    }
                }
                if ((weather.daily.times.size - 1) != index) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = onPrimary,
                        modifier = Modifier
                            .alpha(0.5F)
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}