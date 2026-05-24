package io.github.mamedovilkin.weather

import android.app.Application
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.my.target.common.MyTargetManager
import io.github.mamedovilkin.weather.data.client.GeoCodingHttpClient
import io.github.mamedovilkin.weather.data.client.WeatherHttpClient
import io.github.mamedovilkin.weather.domain.dao.WeatherDao
import io.github.mamedovilkin.weather.data.database.WeatherDatabase
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.data.repository.DataStoreRepositoryImpl
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.data.repository.NetworkRepositoryImpl
import io.github.mamedovilkin.weather.domain.service.LocationService
import io.github.mamedovilkin.weather.data.service.LocationServiceImpl
import io.github.mamedovilkin.weather.domain.usecase.DeleteLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetCurrentLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetCurrentWeatherUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetLocationsUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetPressureUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetTemperatureUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.GetWindSpeedUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.SearchLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.SetLocationUseCase
import io.github.mamedovilkin.weather.domain.usecase.SetPressureUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.SetTemperatureUnitUseCase
import io.github.mamedovilkin.weather.domain.usecase.SetWindSpeedUnitUseCase
import io.github.mamedovilkin.weather.ui.screen.home.HomeViewModel
import io.github.mamedovilkin.weather.ui.screen.locations.LocationsViewModel
import io.github.mamedovilkin.weather.ui.screen.search.SearchViewModel
import io.github.mamedovilkin.weather.ui.screen.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.Locale

class WeatherApp : Application() {

    override fun onCreate() {
        super.onCreate()

        MyTargetManager.initSdk(this)

        startKoin {
            modules(
                module {
                    single<WeatherDao> { WeatherDatabase.getDatabase(this@WeatherApp).weatherDao() }

                    single<LocationService> { LocationServiceImpl(this@WeatherApp) }

                    single<NetworkRepository> { NetworkRepositoryImpl(Geocoder(this@WeatherApp, Locale.getDefault()), WeatherHttpClient.getInstance(), GeoCodingHttpClient.getInstance(), get(), get()) }

                    single<DataStore<Preferences>> { PreferenceDataStoreFactory.create { this@WeatherApp.preferencesDataStoreFile("weather_preferences") } }

                    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }

                    single<GetCurrentLocationUseCase> { GetCurrentLocationUseCase(get()) }

                    single<GetCurrentWeatherUseCase> { GetCurrentWeatherUseCase(get()) }

                    single<GetLocationUseCase> { GetLocationUseCase(get()) }

                    single<GetPressureUnitUseCase> { GetPressureUnitUseCase(get()) }

                    single<GetTemperatureUnitUseCase> { GetTemperatureUnitUseCase(get()) }

                    single<GetWindSpeedUnitUseCase> { GetWindSpeedUnitUseCase(get()) }

                    single<SearchLocationUseCase> { SearchLocationUseCase(get()) }

                    single<SetLocationUseCase> { SetLocationUseCase(get(), get()) }

                    single<SetPressureUnitUseCase> { SetPressureUnitUseCase(get()) }

                    single<SetTemperatureUnitUseCase> { SetTemperatureUnitUseCase(get()) }

                    single<SetWindSpeedUnitUseCase> { SetWindSpeedUnitUseCase(get()) }

                    single<GetLocationsUseCase> { GetLocationsUseCase(get()) }

                    single<DeleteLocationUseCase> { DeleteLocationUseCase(get()) }

                    single<HomeViewModel> { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }

                    single<SearchViewModel> { SearchViewModel(get(), get(), get()) }

                    single<SettingsViewModel> { SettingsViewModel(get(), get(), get(), get(), get(), get()) }

                    single<LocationsViewModel> { LocationsViewModel(get(), get(), get(), get()) }
                }
            )
        }
    }
}