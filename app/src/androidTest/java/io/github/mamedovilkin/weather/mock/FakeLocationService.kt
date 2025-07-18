package io.github.mamedovilkin.weather.mock

import android.location.Location
import io.github.mamedovilkin.weather.service.LocationService

class FakeLocationService : LocationService {

    override fun getCurrentLocation(callback: (Location?) -> Unit) {
        callback(Location.CREATOR.newArray(1).first())
    }
}