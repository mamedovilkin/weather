package io.github.mamedovilkin.weather.network.model

import kotlin.text.lowercase

enum class TemperatureUnit {
    METRIC, IMPERIAL
}

fun String.convertToUnit(): TemperatureUnit {
    return if (this.lowercase() == TemperatureUnit.IMPERIAL.name.lowercase()) {
        TemperatureUnit.IMPERIAL
    } else {
        TemperatureUnit.METRIC
    }
}

fun TemperatureUnit.convertToSymbol(): String {
    return if (this == TemperatureUnit.IMPERIAL) {
        "F"
    } else {
        "C"
    }
}