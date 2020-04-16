package accenturetest.com.weatherforecast.interactors

import accenturetest.com.weatherforecast.data.location.AppLocationRepository
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.ErrorHandler
import accenturetest.com.weatherforecast.util.Resource
import accenturetest.com.weatherforecast.util.tryOrHandle
import javax.inject.Inject

class GetCurrentLocation @Inject constructor(
        private val locationRepository: AppLocationRepository,
        private val errorHandler: ErrorHandler
) {

    suspend operator fun invoke(): Resource<Coordinates> = tryOrHandle(errorHandler) {
        locationRepository.getCurrentLocation()
    }
}