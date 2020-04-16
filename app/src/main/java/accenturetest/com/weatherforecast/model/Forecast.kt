package accenturetest.com.weatherforecast.model

import org.joda.time.DateTime

data class Forecast(
        val id: Int,
        val date: DateTime,
        val temperature: Int,
        val status: String,
        val description: String,
        val maxTemperature: Int,
        val minTemperature: Int
)