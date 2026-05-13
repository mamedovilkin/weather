package io.github.mamedovilkin.weather.domain.model

enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT
}

fun String.convertToTemperatureUnit(): TemperatureUnit {
    return if (this.equals(TemperatureUnit.FAHRENHEIT.name, ignoreCase = true)) {
        TemperatureUnit.FAHRENHEIT
    } else {
        TemperatureUnit.CELSIUS
    }
}

fun TemperatureUnit.convertToSymbol(): String {
    return if (this == TemperatureUnit.FAHRENHEIT) {
        "F"
    } else {
        "C"
    }
}

enum class WindSpeedUnit {
    KMH, MS, MPH
}

fun String.convertToWindSpeedUnit(): WindSpeedUnit {
    return if (this.equals(WindSpeedUnit.KMH.name, ignoreCase = true)) {
        WindSpeedUnit.KMH
    } else if (this.equals(WindSpeedUnit.MPH.name, ignoreCase = true)) {
        WindSpeedUnit.MPH
    } else {
        WindSpeedUnit.MS
    }
}

enum class PressureUnit {
    MB, MMHG
}

fun String.convertToPressureUnit(): PressureUnit {
    return if (this.equals(PressureUnit.MB.name, ignoreCase = true)) {
        PressureUnit.MB
    } else {
        PressureUnit.MMHG
    }
}