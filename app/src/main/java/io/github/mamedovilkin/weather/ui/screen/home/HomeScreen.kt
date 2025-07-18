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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.network.model.CurrentWeather
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.ui.theme.cardBackgroundGradientEnd
import io.github.mamedovilkin.weather.ui.theme.cardBackgroundGradientStart
import io.github.mamedovilkin.weather.ui.theme.primary
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.mamedovilkin.weather.network.model.Weather
import io.github.mamedovilkin.weather.ui.screen.state.ErrorScreen
import io.github.mamedovilkin.weather.ui.screen.state.LoadingScreen
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.surface
import kotlin.math.ceil
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.convertToSymbol
import io.github.mamedovilkin.weather.util.WeatherStat
import io.github.mamedovilkin.weather.util.getSeasonImageOfYear
import io.github.mamedovilkin.weather.util.getWeatherIcon
import kotlinx.serialization.InternalSerializationApi
import java.util.Locale

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
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
                HourlyForecast(screenState.hourlyForecast)
                DailyForecast(screenState.dailyForecast)
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun CurrentWeather(
    @DrawableRes seasonImage: Int,
    weather: CurrentWeather,
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
                    text = weather.date,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primary
                )
                Text(
                    text = weather.weekDay.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = primary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.1F),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(vertical = 2.dp)
                    .padding(horizontal = 8.dp)
                    .clickable { onNavigateToSearch(weather.city) }
                    .testTag("navigate_to_search")
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = weather.city,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = primary
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier
                        .size(18.dp)
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
                        weather.temp,
                        temperatureUnit.convertToSymbol()
                    ),
                    fontSize = 88.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary,
                )
                Text(
                    text = stringResource(R.string.feels_like, weather.description, weather.feelsLikeTemp, temperatureUnit.convertToSymbol()),
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
        modifier = Modifier
            .fillMaxWidth(),
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
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(stat.icon),
                    contentDescription = null,
                    tint = primary
                )
                Text(
                    text = stringResource(stat.title),
                    color = primary,
                )
                Text(
                    text = stat.stat,
                    color = primary,
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
                        text = weather.dt_txt.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = primary
                    )
                    Image(
                        painter = painterResource(getWeatherIcon(weather.weather.first().icon)),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "${ceil(weather.main.temp).toInt()}°",
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
                        text = weather.dt_txt.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = primary,
                        modifier = Modifier.weight(1F)
                    )
                    Image(
                        painter = painterResource(getWeatherIcon(weather.weather.first().icon)),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "${ceil(weather.main.temp_max).toInt()}°",
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
                        text = "${ceil(weather.main.temp_min).toInt()}°",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = onPrimary,
                        modifier = Modifier.weight(1F)
                    )
                    Text(
                        text = weather.weather.first().description,
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