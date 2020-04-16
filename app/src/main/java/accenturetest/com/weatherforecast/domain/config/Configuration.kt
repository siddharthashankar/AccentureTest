package accenturetest.com.weatherforecast.domain.config

import accenturetest.com.weatherforecast.domain.Units

data class Configuration(
        val defaultUnits: Units,
        val lastCurrentWeatherUpdate: Long
)