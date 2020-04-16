package accenturetest.com.weatherforecast.model.transformation

import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import accenturetest.com.weatherforecast.model.Forecast
import accenturetest.com.weatherforecast.model.Weather

object Transformers {

    fun fromDomainWeather(weather: WeatherEntity): Weather = Weather(
            weatherId = weather.weatherId,
            status = weather.status,
            statusDescription = weather.statusDescription.capitalize(),
            temperature = weather.temperature.toInt(),
            maxTemperature = weather.maxTemperature,
            minTemperature = weather.minTemperature,
            units = weather.units,
            locationName = weather.locationName,
            coordinates = weather.coordinates,
            lastUpdate = weather.lastUpdate,
            humidity = weather.humidity?.toInt() ?: 0,
            windSpeed = weather?.windSpeed ?: 0.0
    )

    fun fromDomainForecast(forecastEntity: ForecastEntity): Forecast = Forecast(
            id = forecastEntity.id,
            date = forecastEntity.date,
            temperature = forecastEntity.temperature.toInt(),
            status = forecastEntity.status,
            description = forecastEntity.description,
            maxTemperature = forecastEntity.maxTemperature.toInt(),
            minTemperature = forecastEntity.minTemperature.toInt()
    )
}