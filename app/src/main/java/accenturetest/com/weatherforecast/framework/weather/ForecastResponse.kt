package accenturetest.com.weatherforecast.framework.weather

import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity

data class ForecastResponse(val list: List<ForecastEntity>)