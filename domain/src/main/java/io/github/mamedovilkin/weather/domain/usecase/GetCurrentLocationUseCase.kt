package io.github.mamedovilkin.weather.domain.usecase

import android.location.Location
import io.github.mamedovilkin.weather.domain.service.LocationService

class GetCurrentLocationUseCase(
    private val locationService: LocationService
) {
    operator fun invoke(callback: (Location?) -> Unit) = locationService.getCurrentLocation(callback)
}