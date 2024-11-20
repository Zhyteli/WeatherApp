package com.test.weatherapp.data.repositories

import com.test.weatherapp.data.network.WeatherApiService
import com.test.weatherapp.domain.model.FiveDayForecastResponse
import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : WeatherRepository {
    companion object {
        private const val OPENWEATHER_API_KEY = "3386dc4398331f990a19c5d34b335fc4"
    }

    override suspend fun getCurrentWeather(cityName: String): WeatherResponse {
        return apiService.getCurrentWeather(cityName, OPENWEATHER_API_KEY)
    }

    override suspend fun getCurrentWeatherByCoordinates(lat: Double, lon: Double): WeatherResponse {
        return apiService.getCurrentWeatherByCoordinates(lat, lon, OPENWEATHER_API_KEY)
    }

    override suspend fun getFiveDayForecast(lat: Double, lon: Double): FiveDayForecastResponse {
        return apiService.getFiveDayForecast(lat, lon, OPENWEATHER_API_KEY)
    }
}