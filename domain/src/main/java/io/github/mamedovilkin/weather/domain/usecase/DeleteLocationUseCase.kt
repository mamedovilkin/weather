package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.dao.WeatherDao
import io.github.mamedovilkin.weather.domain.entity.LocationEntity

class DeleteLocationUseCase(
    private val weatherDao: WeatherDao
) {
    suspend operator fun invoke(location: LocationEntity) = weatherDao.deleteLocation(location)
}