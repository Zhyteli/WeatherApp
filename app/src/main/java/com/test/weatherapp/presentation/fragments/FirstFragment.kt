package com.test.weatherapp.presentation.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.test.weatherapp.databinding.FragmentFirstBinding
import com.test.weatherapp.presentation.type.FirstViewEvent
import com.test.weatherapp.presentation.viewmodels.FirstViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirstViewModel by viewModels()

    private val navController by lazy { findNavController() }

    // Для результата диалога настроек геолокации
    private val resolutionForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Пользователь включил необходимые настройки, продолжаем
            viewModel.fetchWeatherByLocation()
        } else {
            // Пользователь отказался включать настройки
            Toast.makeText(requireContext(), "GPS не включен", Toast.LENGTH_LONG).show()
        }
    }

    // Для запроса разрешения на геолокацию
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Разрешение предоставлено, продолжаем
            viewModel.fetchWeatherByLocation()
        } else {
            Toast.makeText(
                requireContext(),
                "Разрешение на геолокацию отклонено",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnCheckCity.setOnClickListener {
            val cityName = binding.etCityName.text.toString().trim()
            if (cityName.isNotEmpty()) {
                viewModel.fetchWeatherByCity(cityName)
            } else {
                Toast.makeText(requireContext(), "Введите название города", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnCheckLocation.setOnClickListener {
            checkLocationPermission()
        }

        observeViewModel()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешения предоставлены, продолжаем
                viewModel.fetchWeatherByLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Показываем объяснение необходимости разрешения
                AlertDialog.Builder(requireContext())
                    .setTitle("Требуется разрешение на геолокацию")
                    .setMessage("Приложению необходимо разрешение на доступ к геолокации для получения погоды по вашему местоположению.")
                    .setPositiveButton("Предоставить") { _, _ ->
                        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }

            else -> {
                // Запрашиваем разрешение
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                // Переход на второй экран с передачей данных
                val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(data)
                navController.navigate(action)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Ошибка: ${exception.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is FirstViewEvent.ShowEnableGpsDialog -> {
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(event.resolvableApiException.resolution)
                                .build()
                        resolutionForResultLauncher.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось открыть настройки",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



