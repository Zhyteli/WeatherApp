package com.test.weatherapp.domain.usecase

import android.location.Location
import com.test.weatherapp.data.repositories.LocationRepositoryImpl
import com.test.weatherapp.domain.repository.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(): Location {
        return locationRepository.getCurrentLocation()
    }
}
