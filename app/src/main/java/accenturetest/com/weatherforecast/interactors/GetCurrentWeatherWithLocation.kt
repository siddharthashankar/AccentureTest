package accenturetest.com.weatherforecast.interactors

import accenturetest.com.weatherforecast.data.location.LocationRepository
import accenturetest.com.weatherforecast.data.weather.WeatherRepository
import accenturetest.com.weatherforecast.domain.ErrorHandler
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import accenturetest.com.weatherforecast.util.Resource
import accenturetest.com.weatherforecast.util.tryOrHandle
import javax.inject.Inject

class GetCurrentWeatherWithLocation @Inject constructor(
        private val weatherRepository: WeatherRepository,
        private val locationRepository: LocationRepository,
        private val errorHandler: ErrorHandler
) {

    suspend operator fun invoke(): Resource<WeatherEntity> = tryOrHandle(errorHandler) {
        weatherRepository.getCurrentWeather(locationRepository.getCurrentLocation())
    }
}