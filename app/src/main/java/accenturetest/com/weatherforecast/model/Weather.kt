package accenturetest.com.weatherforecast.model

import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.Units
import org.joda.time.DateTime

data class Weather(
        val weatherId: Int,
        val status: String,
        val statusDescription: String,
        val temperature: Int,
        val minTemperature: Double,
        val maxTemperature: Double,
        val units: Units,
        val locationName: String,
        val coordinates: Coordinates,
        val lastUpdate: DateTime?,
        val humidity: Int,
        val windSpeed: Double
)