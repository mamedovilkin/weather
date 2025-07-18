package io.github.mamedovilkin.weather.util

import android.content.Context
import android.text.format.DateFormat
import io.github.mamedovilkin.weather.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeatherStat(
    val icon: Int,
    val title: Int,
    val stat: String
)

data class PopularLocation(
    val image: Int,
    val name: String,
    val lat: Double,
    val lon: Double,
)

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

fun isTodayForecast(datetime: String): Boolean {
    val taskCalendar = Calendar.getInstance()
    taskCalendar.timeInMillis = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(datetime)!!.time
    val taskDayOfYear = taskCalendar.get(Calendar.DAY_OF_YEAR)
    val taskYear = taskCalendar.get(Calendar.YEAR)

    val currentCalendar = Calendar.getInstance()
    currentCalendar.timeInMillis = System.currentTimeMillis()
    val currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR)
    val currentYear = currentCalendar.get(Calendar.YEAR)

    return taskDayOfYear == currentDayOfYear && taskYear == currentYear
}

fun getTime(context: Context, datetime: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(datetime)
    return if (DateFormat.is24HourFormat(context)) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date ?: Date())
    } else {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date ?: Date())
    }
}

fun getDate(datetime: String): String {
    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(datetime)
    return SimpleDateFormat("d MMM", Locale.getDefault()).format(date ?: Date())
}

fun String.textWithEllipsis(): String {
    if (this.length > 20) {
        return this.substring(0..20) + "..."
    }

    return this
}

fun getWeatherIcon(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.icon_01d
        "01n" -> R.drawable.icon_01n
        "02d" -> R.drawable.icon_02d
        "02n" -> R.drawable.icon_02n
        "03d" -> R.drawable.icon_03d
        "03n" -> R.drawable.icon_03n
        "04d" -> R.drawable.icon_04d
        "04n" -> R.drawable.icon_04n
        "09d" -> R.drawable.icon_09d
        "09n" -> R.drawable.icon_09n
        "10d" -> R.drawable.icon_10d
        "10n" -> R.drawable.icon_10n
        "11d" -> R.drawable.icon_11d
        "11n" -> R.drawable.icon_11n
        "13d" -> R.drawable.icon_13d
        "13n" -> R.drawable.icon_13n
        "50d" -> R.drawable.icon_50d
        "50n" -> R.drawable.icon_50n
        else -> R.drawable.ic_error
    }
}