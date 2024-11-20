package com.test.weatherapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.test.weatherapp.databinding.ItemHourlyWeatherBinding
import com.test.weatherapp.domain.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HourlyWeatherAdapter :
    ListAdapter<ForecastItem, HourlyWeatherAdapter.HourlyWeatherViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val binding =
            ItemHourlyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class HourlyWeatherViewHolder(private val binding: ItemHourlyWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ForecastItem) {
            // Форматируем время
            val date = Date(item.dt * 1000)
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = sdf.format(date)
            binding.tvTime.text = formattedTime

            // Устанавливаем температуру
            binding.tvTemperature.text = "${item.main.temp.toInt()}°C"

            // Загружаем иконку погоды
            val iconCode = item.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
            binding.ivWeatherIcon.load(iconUrl) {
                crossfade(true)
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_error)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
        override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem == newItem
        }
    }
}

