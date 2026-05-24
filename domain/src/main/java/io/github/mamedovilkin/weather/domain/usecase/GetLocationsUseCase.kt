package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.dao.WeatherDao

class GetLocationsUseCase(
    weatherDao: WeatherDao
) {
    val locations = weatherDao.getLocations()
}