package com.test.weatherapp.domain.usecase

import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): WeatherResponse {
        return repository.getCurrentWeather(cityName)
    }
}
