package com.test.weatherapp.presentation.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.weatherapp.data.repositories.GpsNotEnabledException
import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.domain.usecase.GetCurrentLocationUseCase
import com.test.weatherapp.domain.usecase.GetCurrentLocationWeatherUseCase
import com.test.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.test.weatherapp.presentation.type.FirstViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCurrentLocationWeatherUseCase: GetCurrentLocationWeatherUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
) : ViewModel() {

    private val _weatherData = MutableLiveData<Result<WeatherResponse>>()
    val weatherData: LiveData<Result<WeatherResponse>> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Для событий навигации и показа диалогов
    private val _event = MutableLiveData<FirstViewEvent>()
    val event: LiveData<FirstViewEvent> = _event

    fun fetchWeatherByCity(cityName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = getCurrentWeatherUseCase(cityName)
                _weatherData.value = Result.success(data)
            } catch (e: Exception) {
                _weatherData.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchWeatherByLocation() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val location = getCurrentLocation()
                val data = getCurrentLocationWeatherUseCase(location.latitude, location.longitude)
                _weatherData.value = Result.success(data)
            } catch (e: Exception) {
                when (e) {
                    is GpsNotEnabledException -> {
                        // Отправляем событие во фрагмент для показа диалога
                        _event.value = FirstViewEvent.ShowEnableGpsDialog(e.resolvableApiException)
                    }
                    else -> {
                        _weatherData.value = Result.failure(e)
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getCurrentLocation(): Location {
        var attempts = 3
        while (attempts > 0) {
            try {
                return getCurrentLocationUseCase()
            } catch (e: Exception) {
                if (e is GpsNotEnabledException) {
                    throw e // Пробрасываем исключение вверх
                }
                attempts--
                if (attempts == 0) {
                    throw e
                } else {
                    delay(2000) // Ждем перед повторной попыткой
                }
            }
        }
        throw Exception("Не удалось получить местоположение")
    }
}


