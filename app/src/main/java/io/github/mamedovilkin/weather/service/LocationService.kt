package io.github.mamedovilkin.weather.service

import android.location.Location

interface LocationService {
    fun getCurrentLocation(callback: (Location?) -> Unit)
}