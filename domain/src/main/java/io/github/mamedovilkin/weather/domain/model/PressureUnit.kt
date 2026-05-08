package io.github.mamedovilkin.weather.domain.model

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