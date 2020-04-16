package accenturetest.com.weatherforecast.data.weather

import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity

interface WeatherDataSource {

    suspend fun getCurrentWeatherFromLocal(units: Units): WeatherEntity?

    suspend fun saveCurrentWeatherToLocal(weatherEntity: WeatherEntity)

    suspend fun getCurrentWeatherFromService(coordinates: Coordinates, units: Units): WeatherEntity

    suspend fun getCurrent5DayForecastFromLocal(units: Units): List<ForecastEntity>

    suspend fun getCurrent5DayForecastFromService(coordinates: Coordinates, units: Units): List<ForecastEntity>

    suspend fun saveCurrent5DayForecastToLocal(forecastList: List<ForecastEntity>)
}