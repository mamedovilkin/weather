package io.github.mamedovilkin.weather

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.my.target.common.MyTargetManager
import io.github.mamedovilkin.weather.data.client.WeatherHttpClient
import io.github.mamedovilkin.weather.data.dao.WeatherDao
import io.github.mamedovilkin.weather.data.database.WeatherDatabase
import io.github.mamedovilkin.weather.domain.repository.DataStoreRepository
import io.github.mamedovilkin.weather.data.repository.DataStoreRepositoryImpl
import io.github.mamedovilkin.weather.domain.repository.NetworkRepository
import io.github.mamedovilkin.weather.data.repository.NetworkRepositoryImpl
import io.github.mamedovilkin.weather.domain.usecase.SearchUseCase
import io.github.mamedovilkin.weather.domain.usecase.SettingsUseCase
import io.github.mamedovilkin.weather.domain.service.LocationService
import io.github.mamedovilkin.weather.data.service.LocationServiceImpl
import io.github.mamedovilkin.weather.domain.usecase.HomeUseCase
import io.github.mamedovilkin.weather.domain.usecase.WidgetUseCase
import io.github.mamedovilkin.weather.ui.screen.home.HomeViewModel
import io.github.mamedovilkin.weather.ui.screen.search.SearchViewModel
import io.github.mamedovilkin.weather.ui.screen.settings.SettingsViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.module

class WeatherApp : Application() {

    override fun onCreate() {
        super.onCreate()

        MyTargetManager.initSdk(this)

        startKoin {
            modules(
                module {
                    single<HttpClient> { WeatherHttpClient.getInstance() }

                    single<WeatherDao> { WeatherDatabase.getDatabase(this@WeatherApp).weatherDao() }

                    single<LocationService> { LocationServiceImpl(this@WeatherApp) }

                    single<NetworkRepository> { NetworkRepositoryImpl(get(), get()) }

                    single<DataStore<Preferences>> { PreferenceDataStoreFactory.create { this@WeatherApp.preferencesDataStoreFile("weather_preferences") } }

                    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }

                    single<SettingsUseCase> { SettingsUseCase(get()) }

                    single<SearchUseCase> { SearchUseCase(get(), get()) }

                    single<HomeUseCase> { HomeUseCase(get(), get(), get()) }

                    single<WidgetUseCase> { WidgetUseCase(get(), get()) }

                    single<HomeViewModel> { HomeViewModel(this@WeatherApp, get()) }

                    single<SearchViewModel> { SearchViewModel(get()) }

                    single<SettingsViewModel> { SettingsViewModel(get()) }
                }
            )
        }
    }
}