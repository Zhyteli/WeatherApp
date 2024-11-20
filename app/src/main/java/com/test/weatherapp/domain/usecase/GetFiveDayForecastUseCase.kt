package com.test.weatherapp.domain.usecase

import com.test.weatherapp.domain.model.FiveDayForecastResponse
import com.test.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetFiveDayForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): FiveDayForecastResponse  {
        return weatherRepository.getFiveDayForecast(lat, lon)
    }
}
