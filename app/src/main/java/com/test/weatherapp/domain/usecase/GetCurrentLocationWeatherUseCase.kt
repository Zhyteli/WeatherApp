package com.test.weatherapp.domain.usecase

import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentLocationWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(lat: Double, lon: Double): WeatherResponse {
        return repository.getCurrentWeatherByCoordinates(lat, lon)
    }
}
