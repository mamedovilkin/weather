package io.github.mamedovilkin.weather.util

import android.content.Context
import android.text.format.DateFormat
import io.github.mamedovilkin.weather.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getSeasonImageOfYear(): Int {
    val month = Calendar.getInstance().get(Calendar.MONTH)

    return listOf(
        R.drawable.winter,
        R.drawable.winter,
        R.drawable.spring,
        R.drawable.spring,
        R.drawable.spring,
        R.drawable.summer,
        R.drawable.summer,
        R.drawable.summer,
        R.drawable.autumn,
        R.drawable.autumn,
        R.drawable.autumn,
        R.drawable.winter,
    )[month]
}

fun getTime(context: Context, datetime: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()).parse(datetime)
    return if (DateFormat.is24HourFormat(context)) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date ?: Date())
    } else {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date ?: Date())
    }
}

fun getDate(datetime: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(datetime)
    return SimpleDateFormat("d MMM", Locale.getDefault()).format(date ?: Date())
}

fun getDate(): String {
    val formatter = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    return formatter.format(Date()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun getWeatherIcon(weatherCode: Int): Int {
    return when (weatherCode) {
        0 -> R.drawable.sunny
        1, 2 -> R.drawable.partly_cloudy
        3 -> R.drawable.cloudy
        45, 48 -> R.drawable.foggy
        51, 53, 55, 56, 57, 80, 81, 82 -> R.drawable.rainy
        61, 63, 65, 66, 67 -> R.drawable.partly_rainy
        71, 73, 75, 77, 85, 86 -> R.drawable.snow
        95, 96, 99 ->  R.drawable.thunder
        else -> R.drawable.ic_error
    }
}

fun getWeatherDescription(weatherCode: Int): Int {
    return when (weatherCode) {
        0 -> R.string.clear_sky
        1 -> R.string.mainly_clear
        2 -> R.string.partly_cloudy
        3 -> R.string.overcast
        45, 48 -> R.string.fog
        51 -> R.string.light_drizzle
        53 -> R.string.moderate_drizzle
        55 -> R.string.dense_drizzle
        56, 57 -> R.string.freezing_drizzle
        61 -> R.string.slight_rain
        63 -> R.string.moderate_rain
        65 -> R.string.heavy_rain
        66, 67 -> R.string.freezing_rain
        71 -> R.string.slight_snow
        73 -> R.string.moderate_snow
        75 -> R.string.heavy_snow
        77 -> R.string.snow_grains
        80 -> R.string.slight_rain_showers
        81 -> R.string.moderate_rain_showers
        82 -> R.string.violent_rain_showers
        85, 86 -> R.string.snow_showers
        95 -> R.string.thunderstorm
        96, 99 -> R.string.thunderstorm_wit_hail
        else -> R.string.unknown
    }
}