package io.github.mamedovilkin.weather.ui.widget.forecast

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.domain.model.convertToTemperatureUnit
import io.github.mamedovilkin.weather.domain.model.convertToWindSpeedUnit
import io.github.mamedovilkin.weather.domain.usecase.GetCurrentWeatherUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetTemperatureUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetWindSpeedUnitUseCase
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.util.getDate
import io.github.mamedovilkin.weather.util.getWeatherIcon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.ceil

class ForecastWidget : GlanceAppWidget(), KoinComponent {

    private val getTemperatureUnitUseCase: GetTemperatureUnitUseCase by inject()
    private val getWindSpeedUnitUseCase: GetWindSpeedUnitUseCase by inject()
    private val getLocationUseCase: GetLocationUseCase by inject()
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val context = LocalContext.current
            val temperatureUnit by getTemperatureUnitUseCase.temperatureUnit.collectAsState(null)
            val windSpeedUnit by getWindSpeedUnitUseCase.windSpeedUnit.collectAsState(null)
            val location by getLocationUseCase.location.collectAsState(null)
            val loading = context.getString(R.string.loading)
            var isLoading by remember { mutableStateOf(true) }
            val times = remember { mutableListOf<String>() }
            val weatherCodes = remember { mutableListOf<Int>() }
            val temperatures = remember { mutableListOf<Double>() }

            LaunchedEffect(location) {
                if (temperatureUnit != null && windSpeedUnit != null && location != null) {
                    getCurrentWeatherUseCase(
                        location!!.lat,
                        location!!.lon,
                        temperatureUnit!!.convertToTemperatureUnit(),
                        windSpeedUnit!!.convertToWindSpeedUnit(),
                        5
                    )
                        .onSuccess { weather, _ ->
                            times.addAll(weather.daily.times)
                            weatherCodes.addAll(weather.daily.weatherCodes)
                            temperatures.addAll(weather.daily.maxTemperatures)
                            isLoading = false
                        }
                        .onFailure {
                            isLoading = false
                        }
                }
            }

            GlanceTheme {
                Scaffold(
                    horizontalPadding = 0.dp,
                    backgroundColor = ColorProvider(
                        day = background,
                        night = background,
                    ),
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .clickable(actionRunCallback<RefreshAction>()),
                ) {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize()
                    ) {
                        if (isLoading) {
                            Text(
                                text = loading,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(
                                        day = Color.White,
                                        night = Color.White
                                    )
                                )
                            )
                        } else {
                            LazyVerticalGrid(
                                gridCells = GridCells.Fixed(times.size),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = GlanceModifier
                                    .fillMaxSize()
                                    .padding(vertical = 4.dp)
                            ) {
                                items(times.size) { index ->
                                    Column(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = getDate(times[index]),
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                color = ColorProvider(
                                                    day = Color.White,
                                                    night = Color.White
                                                )
                                            )
                                        )
                                        Image(
                                            provider = ImageProvider(getWeatherIcon(weatherCodes[index], 1)),
                                            contentScale = ContentScale.Crop,
                                            contentDescription = null,
                                            modifier = GlanceModifier
                                                .padding(4.dp)
                                                .size(36.dp)
                                        )
                                        Text(
                                            text = "${ceil(temperatures[index]).toInt()}°",
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center,
                                                color = ColorProvider(
                                                    day = Color.White,
                                                    night = Color.White
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}