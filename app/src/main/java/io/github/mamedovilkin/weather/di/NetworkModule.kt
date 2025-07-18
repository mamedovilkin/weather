package io.github.mamedovilkin.weather.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.mamedovilkin.weather.network.client.WeatherHttpClient
import io.github.mamedovilkin.weather.network.repository.NetworkRepository
import io.github.mamedovilkin.weather.network.repository.NetworkRepositoryImpl
import io.github.mamedovilkin.weather.service.LocationService
import io.github.mamedovilkin.weather.service.LocationServiceImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkRepository(): NetworkRepository {
        return NetworkRepositoryImpl(WeatherHttpClient.getInstance())
    }

    @Provides
    @Singleton
    fun provideDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }

    @Provides
    @Singleton
    fun provideLocationService(@ApplicationContext context: Context): LocationService {
        return LocationServiceImpl(context)
    }
}