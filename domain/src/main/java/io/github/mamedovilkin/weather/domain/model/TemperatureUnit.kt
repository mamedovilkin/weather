package io.github.mamedovilkin.weather.domain.model

enum class TemperatureUnit {
    METRIC, IMPERIAL
}

fun String.convertToTemperatureUnit(): TemperatureUnit {
    return if (this.equals(TemperatureUnit.IMPERIAL.name, ignoreCase = true)) {
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