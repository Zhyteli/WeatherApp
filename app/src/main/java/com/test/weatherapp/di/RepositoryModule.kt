package com.test.weatherapp.di

import android.content.Context
import com.test.weatherapp.data.network.WeatherApiService
import com.test.weatherapp.data.repositories.LocationRepositoryImpl
import com.test.weatherapp.data.repositories.WeatherRepositoryImpl
import com.test.weatherapp.domain.repository.LocationRepository
import com.test.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideWeatherRepository(apiService: WeatherApiService): WeatherRepository =
        WeatherRepositoryImpl(apiService)

    @Provides
    fun provideLocationRepository(@ApplicationContext context: Context): LocationRepository =
        LocationRepositoryImpl(context)
}
