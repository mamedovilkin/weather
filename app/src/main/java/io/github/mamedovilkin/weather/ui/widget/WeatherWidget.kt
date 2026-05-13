package io.github.mamedovilkin.weather.ui.widget

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
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
import io.github.mamedovilkin.weather.domain.model.convertToSymbol
import io.github.mamedovilkin.weather.domain.model.convertToTemperatureUnit
import io.github.mamedovilkin.weather.domain.model.convertToWindSpeedUnit
import io.github.mamedovilkin.weather.domain.usecase.WidgetUseCase
import io.github.mamedovilkin.weather.util.getSeasonImageOfYear
import io.github.mamedovilkin.weather.util.getWeatherDescription
import io.github.mamedovilkin.weather.util.getWeatherIcon
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.ceil

class WeatherWidget : GlanceAppWidget(), KoinComponent {

    private val widgetUseCase: WidgetUseCase by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val temperatureUnit by widgetUseCase.temperatureUnit.collectAsState(null)
            val windSpeedUnit by widgetUseCase.windSpeedUnit.collectAsState(null)
            val location by widgetUseCase.location.collectAsState(null)
            val loading = context.getString(R.string.loading)
            var isLoading by remember { mutableStateOf(true) }
            var weatherCode by remember { mutableIntStateOf(0) }
            var temperature by remember { mutableDoubleStateOf(0.0) }
            var feelsLike by remember { mutableDoubleStateOf(0.0) }

            LaunchedEffect(location) {
                if (temperatureUnit != null && windSpeedUnit != null && location != null) {
                    widgetUseCase
                        .getCurrentWeather(location!!.first(), location!!.last(), temperatureUnit!!.convertToTemperatureUnit(), windSpeedUnit!!.convertToWindSpeedUnit())
                        .onSuccess { weather ->
                            weatherCode = weather.weatherCode
                            temperature = weather.temperature
                            feelsLike = weather.feelsLike
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
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .clickable {
                            coroutineScope.launch {
                                WeatherWidget().updateAll(context)
                            }
                        },
                ) {
                    Box {
                        Image(
                            provider = ImageProvider(getSeasonImageOfYear()),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = GlanceModifier.fillMaxSize()
                        )
                        Box(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .background(R.color.widget)
                        ) {}
                        if (isLoading) {
                            Column(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = GlanceModifier.fillMaxSize()
                            ) {
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
                            }
                        } else {
                            Column(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = GlanceModifier.fillMaxSize()
                            ) {
                                Image(
                                    provider = ImageProvider(getWeatherIcon(weatherCode)),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = GlanceModifier
                                        .size(80.dp)
                                        .padding(4.dp),
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = if (location != null) context.getString(R.string.temperature, ceil(temperature).toInt().toString(), temperatureUnit?.convertToTemperatureUnit()?.convertToSymbol() ?: "") else "-°-",
                                        style = TextStyle(
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorProvider(
                                                day = Color.White,
                                                night = Color.White
                                            )
                                        ),
                                    )
                                    Text(
                                        text = if (location != null) context.getString(R.string.widget_summary, context.getString(getWeatherDescription(weatherCode)).lowercase(), ceil(feelsLike).toInt().toString(), temperatureUnit?.convertToTemperatureUnit()?.convertToSymbol() ?: "") else "—",
                                        style = TextStyle(
                                            textAlign = TextAlign.Center,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
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