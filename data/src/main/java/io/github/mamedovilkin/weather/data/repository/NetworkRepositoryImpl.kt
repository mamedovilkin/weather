package io.github.mamedovilkin.weather.data.repository

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import io.github.mamedovilkin.weather.domain.dao.WeatherDao
import io.github.mamedovilkin.weather.data.mapper.toDomainLocation
import io.github.mamedovilkin.weather.data.mapper.toDomainWeather
import io.github.mamedovilkin.weather.data.mapper.toEntityWeather
import io.github.mamedovilkin.weather.data.model.SearchDto
import io.github.mamedovilkin.weather.data.model.WeatherDto
import io.github.mamedovilkin.weather.data.util.isInternetAvailable
import io.github.mamedovilkin.weather.domain.model.Location
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.Weather
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.domain.util.Result
import io.github.mamedovilkin.weather.domain.util.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

class NetworkRepositoryImpl(
    private val geocoder: Geocoder,
    private val weatherHttpClient: HttpClient,
    private val geocodingHttpClient: HttpClient,
    private val weatherDao: WeatherDao,
    private val dataStoreRepository: DataStoreRepository
) : NetworkRepository {

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit,
        windSpeedUnit: WindSpeedUnit,
        forecastDays: Int
    ): Result<Weather> {
        return try {
            var name = getCityName(lat, lon)

            if (name != null) {
                name = geocodingHttpClient
                    .get("v1/search?name=$name&count=1&language=${Locale.getDefault().language}&format=json")
                    .body<SearchDto>()
                    .results
                    .first()
                    .toDomainLocation()
                    .name
            }

            val weatherEntity = weatherHttpClient
                .get("v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,apparent_temperature,weather_code,wind_speed_10m,relative_humidity_2m,surface_pressure,is_day&hourly=temperature_2m,weather_code,is_day&daily=temperature_2m_max,temperature_2m_min,weather_code,sunrise,sunset,uv_index_max&forecast_days=$forecastDays&temperature_unit=${temperatureUnit.name.lowercase()}&wind_speed_unit=${windSpeedUnit.name.lowercase()}&timezone=${TimeZone.getDefault().id}")
                .body<WeatherDto>()
                .toEntityWeather(name)

            weatherDao.insertWeather(weatherEntity)

            dataStoreRepository.setLocation(weatherEntity.name ?: "", weatherEntity.latitude, weatherEntity.longitude)

            val weather = weatherDao.getWeather()

            Result.Success(weather.toDomainWeather(), !isInternetAvailable())
        } catch (e: Exception) {
            try {
                val weather = weatherDao.getWeather()
                Result.Success(weather.toDomainWeather(), !isInternetAvailable())
            } catch (_: Exception) {
                Result.Failure(e)
            }
        }
    }

    suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): String? {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getCityNameModern(latitude, longitude)
        } else {
            getCityNameLegacy(latitude, longitude)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getCityNameModern(
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

                    if (continuation.isActive) {
                        continuation.resume(city)
                    }
                }

                override fun onError(
                    errorMessage: String?
                ) {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
        )
    }

    @Suppress("DEPRECATION")
    private suspend fun getCityNameLegacy(
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {
        runCatching {
            geocoder.getFromLocation(latitude, longitude, 1)
                ?.firstOrNull()
                ?.locality
        }.getOrNull()
    }

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        val formattedQuery = query.replace(" ", "%20")

        return safeRequest ({
            geocodingHttpClient
                .get("v1/search?name=$formattedQuery&count=10&language=${Locale.getDefault().language}&format=json")
                .body<SearchDto>()
                .results
                .map { it.toDomainLocation() }
        }, false)
    }
}