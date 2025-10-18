package io.github.mamedovilkin.weather.domain.service

import android.location.Location

interface LocationService {
    fun getCurrentLocation(callback: (Location?) -> Unit)
}