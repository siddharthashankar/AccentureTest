package accenturetest.com.weatherforecast.data.weather

import accenturetest.com.weatherforecast.data.config.ConfigurationDataSource
import accenturetest.com.weatherforecast.data.exceptions.ConnectivityException
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.config.NetworkStatus
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import javax.inject.Inject

class AppWeatherRepository @Inject constructor(
        private val dataSource: WeatherDataSource,
        private val configurationDataSource: ConfigurationDataSource
): WeatherRepository {

    /* 3 hours. */
    private val threshold: Long = 3 * 60 * 60 * 1000

    override suspend fun getCurrentWeather(coordinates: Coordinates): WeatherEntity {
        val networkStatus = configurationDataSource.getNetworkStatus()
        val config = configurationDataSource.getConfiguration()
        val savedUnits = config.defaultUnits
        val savedWeather = dataSource.getCurrentWeatherFromLocal(savedUnits)
        if (savedWeather == null && networkStatus == NetworkStatus.NOT_CONNECTED) {
            throw ConnectivityException()
        }
        if (savedWeather == null || config.lastCurrentWeatherUpdate - System.currentTimeMillis() > threshold) {
            val retrievedWeather = dataSource.getCurrentWeatherFromService(coordinates, savedUnits)
            configurationDataSource.saveLastUpdate(System.currentTimeMillis())
            dataSource.saveCurrentWeatherToLocal(retrievedWeather)
        }
        return dataSource.getCurrentWeatherFromLocal(savedUnits) ?: throw IllegalStateException()
    }

    override suspend fun get5DayCurrentForecast(coordinates: Coordinates): List<ForecastEntity> {
        val networkStatus = configurationDataSource.getNetworkStatus()
        val config = configurationDataSource.getConfiguration()
        val savedUnits = config.defaultUnits
        val savedForecast = dataSource.getCurrent5DayForecastFromLocal(savedUnits)
        if (savedForecast.isEmpty() && networkStatus == NetworkStatus.NOT_CONNECTED) {
            throw ConnectivityException()
        }
        if (config.lastCurrentWeatherUpdate - System.currentTimeMillis() > threshold) {
            val retrievedForecast =
                    dataSource.getCurrent5DayForecastFromService(coordinates, savedUnits)
            dataSource.saveCurrent5DayForecastToLocal(retrievedForecast)
        }
        return dataSource.getCurrent5DayForecastFromLocal(savedUnits)
    }
}