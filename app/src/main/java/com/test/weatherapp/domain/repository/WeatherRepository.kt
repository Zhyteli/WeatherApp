package com.test.weatherapp.domain.repository

import com.test.weatherapp.domain.model.FiveDayForecastResponse
import com.test.weatherapp.domain.model.WeatherResponse

interface WeatherRepository {
    suspend fun getCurrentWeather(cityName: String): WeatherResponse
    suspend fun getCurrentWeatherByCoordinates(lat: Double, lon: Double): WeatherResponse
    suspend fun getFiveDayForecast(lat: Double, lon: Double): FiveDayForecastResponse
}