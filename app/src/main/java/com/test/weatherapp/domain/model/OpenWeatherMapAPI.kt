package com.test.weatherapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class WeatherResponse(
    val coord: Coord,
    val name: String,
    val main: Main,
    val pop: Double,
    val weather: List<Weather>,
    val wind: Wind,
) : Parcelable

@Parcelize
data class Coord(
    val lon: Double,
    val lat: Double
) : Parcelable

@Parcelize
data class Main(
    val temp: Double,
    val humidity: Int,
    val temp_min: Double, // Минимальная температура
    val temp_max: Double, // Максимальная температура
) : Parcelable


@Parcelize
data class Wind(
    val speed: Double,
    val deg: Int
) : Parcelable

@Parcelize
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) : Parcelable


@Parcelize
data class FiveDayForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
) : Parcelable

@Parcelize
data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
//    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
//    val sys: Sys,
    val dt_txt: String
) : Parcelable

@Parcelize
data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
) : Parcelable


data class DailyForecast(
    val date: Date,
    val minTemp: Double,
    val maxTemp: Double,
    val weatherIcon: String
)
