package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.dao.WeatherDao
import io.github.mamedovilkin.weather.domain.entity.LocationEntity
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class SetLocationUseCase(
    private val dataStoreRepository: DataStoreRepository,
    private val weatherDao: WeatherDao,
) {
    suspend operator fun invoke(name: String, lat: Double, lon: Double) {
        dataStoreRepository.setLocation(name, lat, lon)
        weatherDao.insertLocation(LocationEntity(0, name, lat, lon))
    }
}