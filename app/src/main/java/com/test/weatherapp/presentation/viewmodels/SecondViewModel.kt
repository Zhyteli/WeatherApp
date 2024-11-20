package com.test.weatherapp.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.weatherapp.domain.model.DailyForecast
import com.test.weatherapp.domain.model.FiveDayForecastResponse
import com.test.weatherapp.domain.model.ForecastItem
import com.test.weatherapp.domain.usecase.GetFiveDayForecastUseCase
import com.test.weatherapp.presentation.type.ForecastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(
    private val getFiveDayForecastUseCase: GetFiveDayForecastUseCase
) : ViewModel() {

    private val _hourlyWeatherData = MutableLiveData<Result<List<ForecastItem>>>()
    val hourlyWeatherData: LiveData<Result<List<ForecastItem>>> = _hourlyWeatherData

    private val _dailyWeatherData = MutableLiveData<Result<List<DailyForecast>>>()
    val dailyWeatherData: LiveData<Result<List<DailyForecast>>> = _dailyWeatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _forecastType = MutableLiveData<ForecastType>(ForecastType.HOURLY)
    val forecastType: LiveData<ForecastType> = _forecastType

    fun fetchForecast(lat: Double, lon: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = getFiveDayForecastUseCase(lat, lon)
                // Почасовой прогноз на следующие 8 периодов (24 часа)
                val hourlyData = response.list.take(8)
                _hourlyWeatherData.value = Result.success(hourlyData)

                // Обработка данных для ежедневного прогноза
                val dailyData = processDailyForecast(response)
                _dailyWeatherData.value = Result.success(dailyData)
            } catch (e: Exception) {
                _hourlyWeatherData.value = Result.failure(e)
                _dailyWeatherData.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun processDailyForecast(response: FiveDayForecastResponse): List<DailyForecast> {
        // Группировка по дню
        val groupedData = response.list.groupBy { forecastItem ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = forecastItem.dt * 1000L
            calendar.get(Calendar.DAY_OF_YEAR)
        }

        val dailyForecasts = groupedData.values.map { dayForecastItems ->
            val minTemp = dayForecastItems.minOf { it.main.temp_min }
            val maxTemp = dayForecastItems.maxOf { it.main.temp_max }
            val date = Date(dayForecastItems[0].dt * 1000L)
            val weatherIcon = dayForecastItems[0].weather[0].icon
            DailyForecast(
                date = date,
                minTemp = minTemp,
                maxTemp = maxTemp,
                weatherIcon = weatherIcon
            )
        }

        // Исключаем текущий день, если необходимо
        return dailyForecasts.drop(1).take(4) // Следующие 4 дня
    }

    fun toggleForecastType() {
        _forecastType.value = when (_forecastType.value) {
            ForecastType.HOURLY -> ForecastType.DAILY
            ForecastType.DAILY -> ForecastType.HOURLY
            else -> ForecastType.HOURLY
        }
    }
}


