package com.test.weatherapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.test.weatherapp.databinding.ItemDailyWeatherBinding
import com.test.weatherapp.domain.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Locale

class DailyWeatherAdapter : ListAdapter<DailyForecast, DailyWeatherAdapter.DailyWeatherViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val binding = ItemDailyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DailyWeatherViewHolder(private val binding: ItemDailyWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailyForecast) {
            // Установка дня недели
            val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
            val dayOfWeek = sdf.format(item.date)
            binding.tvDay.text = dayOfWeek.replaceFirstChar { it.uppercase() }

            // Загрузка иконки погоды
            val iconCode = item.weatherIcon
            val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
            binding.ivWeatherIcon.load(iconUrl) {
                crossfade(true)
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_error)
            }

            // Установка минимальной и максимальной температуры
            binding.tvMinTemp.text = "Мин: ${item.minTemp.toInt()}°C"
            binding.tvMaxTemp.text = "Макс: ${item.maxTemp.toInt()}°C"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }
}
