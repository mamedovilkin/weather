package io.github.mamedovilkin.weather.domain.usecase

import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository

class SettingsUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    val temperatureUnit = dataStoreRepository.temperatureUnit

    val windSpeedUnit = dataStoreRepository.windSpeedUnit

    val pressureUnit = dataStoreRepository.pressureUnit

    suspend fun setTemperatureUnit(temperatureUnit: TemperatureUnit) = dataStoreRepository.setTemperatureUnit(temperatureUnit.name)

    suspend fun setWindSpeedUnit(windSpeedUnit: WindSpeedUnit) = dataStoreRepository.setWindSpeedUnit(windSpeedUnit.name)

    suspend fun setPressureUnit(pressureUnit: PressureUnit) = dataStoreRepository.setPressureUnit(pressureUnit.name)
}