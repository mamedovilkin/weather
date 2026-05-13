package io.github.mamedovilkin.weather.data.repository

import android.location.Address
import android.location.Geocoder
import io.github.mamedovilkin.weather.data.dao.WeatherDao
import io.github.mamedovilkin.weather.data.mapper.toDomainLocation
import io.github.mamedovilkin.weather.data.mapper.toDomainWeather
import io.github.mamedovilkin.weather.data.mapper.toEntityWeather
import io.github.mamedovilkin.weather.data.model.SearchDto
import io.github.mamedovilkin.weather.data.model.WeatherDto
import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.domain.util.Result
import io.github.mamedovilkin.weather.domain.util.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class NetworkRepositoryImpl(
    private val geocoder: Geocoder,
    private val weatherHttpClient: HttpClient,
    private val geocodingHttpClient: HttpClient,
    private val weatherDao: WeatherDao
) : NetworkRepository {

    val lang = if (Locale.getDefault().language == "ru") "ru" else "en"

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit
    ): Result<Weather> {
        return safeRequest {
            try {
                var name = getCityName(lat, lon)

                if (name != null) {
                    name = geocodingHttpClient
                        .get("v1/search?name=$name&count=1&language=$lang&format=json")
                        .body<SearchDto>()
                        .results
                        .first()
                        .toDomainLocation()
                        .name
                }

                val weatherEntity = weatherHttpClient
                    .get("v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,apparent_temperature,weather_code,wind_speed_10m,relative_humidity_2m,surface_pressure&hourly=temperature_2m,weather_code&daily=temperature_2m_max,temperature_2m_min,weather_code&forecast_days=16&temperature_unit=${temperatureUnit.name.lowercase()}&wind_speed_unit=${windSpeedUnit.name.lowercase()}")
                    .body<WeatherDto>()
                    .toEntityWeather(name)

                weatherDao.insertWeather(weatherEntity)

                val weather = weatherDao.getWeather()

                return Result.Success(weather.toDomainWeather())
            } catch (e: Exception) {
                try {
                    val weather = weatherDao.getWeather()
                    return Result.Success(weather.toDomainWeather())
                } catch (_: Exception) {
                    return Result.Failure(e)
                }
            }
        }
    }

    suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): String? = suspendCancellableCoroutine { continuation ->
        geocoder.getFromLocation(
            latitude,
            longitude,
            1,
            object : Geocoder.GeocodeListener {

                override fun onGeocode(
                    addresses: MutableList<Address>
                ) {
                    val city = addresses
                        .firstOrNull()
                        ?.locality
                    continuation.resume(city)
                }

                override fun onError(
                    errorMessage: String?
                ) {
                    continuation.resume(null)
                }
            }
        )
    }

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        val formattedQuery = query.replace(" ", "%20")

        return safeRequest {
            geocodingHttpClient
                .get("v1/search?name=$formattedQuery&count=10&language=$lang&format=json")
                .body<SearchDto>()
                .results
                .map { it.toDomainLocation() }
        }
    }
}