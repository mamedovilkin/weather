package io.github.mamedovilkin.weather.mock

import io.github.mamedovilkin.weather.network.client.Result
import io.github.mamedovilkin.weather.network.model.Forecast
import io.github.mamedovilkin.weather.network.model.Location
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.Weather
import io.github.mamedovilkin.weather.network.repository.NetworkRepository

class FakeFailureNetworkRepository : NetworkRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Weather> {
        return Result.Failure(
            e = Exception()
        )
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Forecast> {
        return Result.Failure(
            e = Exception()
        )
    }

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        return Result.Failure(
            e = Exception()
        )
    }
}