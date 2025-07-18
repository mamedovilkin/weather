package io.github.mamedovilkin.weather.mock

import io.github.mamedovilkin.weather.network.client.Result
import io.github.mamedovilkin.weather.network.model.Forecast
import io.github.mamedovilkin.weather.network.model.Icon
import io.github.mamedovilkin.weather.network.model.Location
import io.github.mamedovilkin.weather.network.model.Main
import io.github.mamedovilkin.weather.network.model.Sys
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.network.model.Weather
import io.github.mamedovilkin.weather.network.model.Wind
import io.github.mamedovilkin.weather.network.repository.NetworkRepository

class FakeSuccessNetworkRepository : NetworkRepository {

    private val weather = Weather(
        dt = 0,
        main = Main(
            temp = 0.0,
            feels_like = 0.0,
            temp_min = 0.0,
            temp_max = 0.0,
            humidity = 0,
            pressure = 0
        ),
        name = "New York",
        sys = Sys(country = "US"),
        wind = Wind(speed = 0.0),
        weather = listOf(Icon(
            main = "Clouds",
            description = "",
            icon = ""
        )),
        dt_txt = "0000-00-00 00:00:00"
    )

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Weather> {
        return Result.Success<Weather>(weather)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit
    ): Result<Forecast> {
        return Result.Success<Forecast>(
            Forecast(
                list = listOf(weather)
            )
        )
    }

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        return Result.Success<List<Location>>(
            listOf(
                Location(
                    name = "New York",
                    country = "US",
                    state = "NY",
                    lat = 50.0,
                    lon = 50.0
                )
            )
        )
    }
}