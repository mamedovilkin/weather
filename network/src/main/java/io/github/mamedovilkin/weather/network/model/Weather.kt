@file:Suppress("PropertyName")
@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@Serializable
data class Weather(
    val dt: Long,
    val main: Main,
    val name: String? = null,
    val sys: Sys,
    val wind: Wind,
    val weather: List<Icon>,
    val dt_txt: String? = null,
)

fun Weather.toCurrentWeather(): CurrentWeather {
    return CurrentWeather(
        icon = this.weather.first().icon,
        date = getFullDate(),
        weekDay = getWeekDay(),
        city = this.name.toString(),
        countryState = this.sys.country.toString(),
        temp = ceil(this.main.temp).toInt(),
        feelsLikeTemp = ceil(this.main.feels_like).toInt(),
        description = this.weather.first().description
    )
}

private fun getFullDate(): String {
    val formatter = SimpleDateFormat("d MMMM, ", Locale.getDefault())
    return formatter.format(Date())
}

private fun getWeekDay(): String {
    val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
    return formatter.format(Date())
}