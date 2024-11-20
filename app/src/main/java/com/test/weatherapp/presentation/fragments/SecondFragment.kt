package com.test.weatherapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.test.weatherapp.R
import com.test.weatherapp.databinding.FragmentSecondBinding
import com.test.weatherapp.domain.model.WeatherResponse
import com.test.weatherapp.presentation.adapter.DailyWeatherAdapter
import com.test.weatherapp.presentation.adapter.HourlyWeatherAdapter
import com.test.weatherapp.presentation.type.ForecastType
import com.test.weatherapp.presentation.viewmodels.SecondViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val args: SecondFragmentArgs by navArgs()
    private val viewModel: SecondViewModel by viewModels()

    private lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    private lateinit var dailyWeatherAdapter: DailyWeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val weatherData = args.weatherData

        // Установка данных в UI
        binding.tvTemperature.text = "${weatherData.main.temp.toInt()}°C"
        binding.tvLocation.text = weatherData.name
        binding.tvDescription.text = weatherData.weather[0].description.capitalize(Locale.getDefault())
        binding.tvAdditionalData.text = "Влажность: ${weatherData.main.humidity}%  Ветер: ${weatherData.wind.speed} м/с"

        // Загрузка иконки погоды
        val iconCode = weatherData.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
        binding.ivWeatherIcon.load(iconUrl)

        // Инициализация адаптеров
        hourlyWeatherAdapter = HourlyWeatherAdapter()
        dailyWeatherAdapter = DailyWeatherAdapter()

        // Начальная настройка RecyclerView
        binding.rvWeather.apply {
            adapter = hourlyWeatherAdapter // Устанавливаем адаптер для почасового прогноза
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Обработчик нажатия на кнопку
        binding.btnToggleForecast.setOnClickListener {
            viewModel.toggleForecastType()
        }

        // Подписка на изменения ViewModel
        observeViewModel()

        // Получение прогноза погоды
        viewModel.fetchForecast(weatherData.coord.lat, weatherData.coord.lon)
    }

    private fun observeViewModel() {
        viewModel.hourlyWeatherData.observe(viewLifecycleOwner) { result ->
            result.onSuccess { hourlyData ->
                if (viewModel.forecastType.value == ForecastType.HOURLY) {
                    hourlyWeatherAdapter.submitList(hourlyData)
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Ошибка: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.dailyWeatherData.observe(viewLifecycleOwner) { result ->
            result.onSuccess { dailyData ->
                if (viewModel.forecastType.value == ForecastType.DAILY) {
                    dailyWeatherAdapter.submitList(dailyData)
                }
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Ошибка: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.forecastType.observe(viewLifecycleOwner) { forecastType ->
            when (forecastType) {
                ForecastType.HOURLY -> {
                    // Устанавливаем горизонтальный LinearLayoutManager
                    binding.rvWeather.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.rvWeather.adapter = hourlyWeatherAdapter
                    binding.btnToggleForecast.text = "Следующий прогноз"
                    // Устанавливаем данные в адаптер
                    viewModel.hourlyWeatherData.value?.onSuccess { hourlyData ->
                        hourlyWeatherAdapter.submitList(hourlyData)
                    }
                }
                ForecastType.DAILY -> {
                    // Устанавливаем вертикальный LinearLayoutManager
                    binding.rvWeather.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvWeather.adapter = dailyWeatherAdapter
                    binding.btnToggleForecast.text = "Домой"
                    // Устанавливаем данные в адаптер
                    viewModel.dailyWeatherData.value?.onSuccess { dailyData ->
                        dailyWeatherAdapter.submitList(dailyData)
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
