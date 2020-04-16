package accenturetest.com.weatherforecast.data.weather

import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity

interface WeatherRepository {

    suspend fun getCurrentWeather(coordinates: Coordinates): WeatherEntity

    suspend fun get5DayCurrentForecast(coordinates: Coordinates): List<ForecastEntity>
}