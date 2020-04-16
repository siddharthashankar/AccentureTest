package accenturetest.com.weatherforecast

import com.nhaarman.mockitokotlin2.*
import com.nhaarman.mockitokotlin2.internal.createInstance
import accenturetest.com.weatherforecast.data.config.ConfigurationDataSource
import accenturetest.com.weatherforecast.data.exceptions.ConnectivityException
import accenturetest.com.weatherforecast.data.weather.AppWeatherRepository
import accenturetest.com.weatherforecast.data.weather.WeatherDataSource
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.domain.config.Configuration
import accenturetest.com.weatherforecast.domain.config.NetworkStatus
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import kotlin.reflect.KClass

class AppWeatherRepositoryTest {

    private lateinit var SUT: AppWeatherRepository
    private lateinit var weatherSrcMock: WeatherDataSource
    private lateinit var configSrcMock: ConfigurationDataSource
    private lateinit var weatherEntityMock: WeatherEntity
    private lateinit var forecastListMock: MutableList<ForecastEntity>
    private lateinit var configurationMock: Configuration

    @Before
    fun setup() {
        weatherSrcMock = mock()
        configSrcMock = mock()
        configurationMock = Configuration(Units.SI, System.currentTimeMillis())
        whenever(configSrcMock.getConfiguration()).thenReturn(configurationMock)
        weatherEntityMock = WeatherEntity(
                1,
                "",
                "",
                1.0,
                1.0,
                1.0,
                Units.IMPERIAL,
                "MX",
                Coordinates(1.1, -1.0),
                null,
                1,
                1.1
        )
        forecastListMock = mutableListOf(
                ForecastEntity(
                        1,
                        DateTime(),
                        1.1,
                        "",
                        "", 1.0, -1.0)
        )
        SUT = AppWeatherRepository(weatherSrcMock, configSrcMock)
    }

    @Test
    fun getCurrentWeather() {
        runBlocking {
            try {
                weatherSrcLocalEmpty()
                notConnected()
                SUT.getCurrentWeather(Coordinates(0.0, 0.0))
                verify(weatherSrcMock, times(1)).getCurrentWeatherFromLocal(any())
                verify(configSrcMock, times(1)).getConfiguration()
                verify(configSrcMock, times(1)).getNetworkStatus()
            } catch (exception: Exception) {
                assert(exception is ConnectivityException)
            }
            // TODO: Case when the time difference between the last update is greater than the threshold.
            // In that case, the repository should only check for internet connection
            // If there is internet connection then the repository gets the most recent
            // weather for the "current" location.
            // Then it must be saved to the local.
            thresholdExpired()
            connected()
            weatherSrcSuccessFromService()
            weatherSrcLocalNotEmpty()
            argumentCaptor<WeatherEntity> {
                val result = SUT.getCurrentWeather(Coordinates(0.0, 0.0))
                verify(configSrcMock, times(2)).getConfiguration()
                verify(configSrcMock, times(2)).getNetworkStatus()
                verify(weatherSrcMock, times(1)).getCurrentWeatherFromService(any(), any())
                verify(weatherSrcMock, times(1)).saveCurrentWeatherToLocal(capture())
                verify(configSrcMock, times(1)).saveLastUpdate(any())
                verify(weatherSrcMock, times(3)).getCurrentWeatherFromLocal(any())
                assertEquals(firstValue, result)
            }
            thresholdNotExpired()
            connected()
            argumentCaptor<WeatherEntity> {
                val result = SUT.getCurrentWeather(Coordinates(0.0, 0.0))
                verify(configSrcMock, times(3)).getConfiguration()
                verify(configSrcMock, times(3)).getNetworkStatus()
                verify(weatherSrcMock, times(1)).getCurrentWeatherFromService(any(), any())
                verify(weatherSrcMock, times(1)).saveCurrentWeatherToLocal(any())
                verify(configSrcMock, times(1)).saveLastUpdate(any())
                verify(weatherSrcMock, times(5)).getCurrentWeatherFromLocal(any())
                assertEquals(weatherEntityMock, result)
            }
        }
    }

    @Test
    fun get5DayCurrentForecast() {
        // TODO
        runBlocking {
            try {
                forecastSrcLocalEmpty()
                notConnected()
                SUT.get5DayCurrentForecast(Coordinates(0.0, 0.0))
                verify(weatherSrcMock, times(1)).getCurrent5DayForecastFromLocal(any())
                verify(configSrcMock, times(1)).getConfiguration()
                verify(configSrcMock, times(1)).getNetworkStatus()
            } catch (exception: Exception) {
                assert(exception is ConnectivityException)
            }
            thresholdExpired()
            connected()
            forecastSrcSuccessFromService()
            forecastSrcLocalNotEmpty()
            argumentCaptor<List<ForecastEntity>> {
                val result = SUT.get5DayCurrentForecast(Coordinates(0.0, 0.0))
                verify(configSrcMock, times(2)).getConfiguration()
                verify(configSrcMock, times(2)).getNetworkStatus()
                verify(weatherSrcMock, times(1)).getCurrent5DayForecastFromService(any(), any())
                verify(weatherSrcMock, times(1)).saveCurrent5DayForecastToLocal(capture())
                verify(weatherSrcMock, times(3)).getCurrent5DayForecastFromLocal(any())
                assertEquals(firstValue, result)
            }
            thresholdNotExpired()
            connected()
            val result = SUT.get5DayCurrentForecast(Coordinates(0.0, 0.0))
            verify(configSrcMock, times(3)).getConfiguration()
            verify(configSrcMock, times(3)).getNetworkStatus()
            verify(weatherSrcMock, times(1)).getCurrent5DayForecastFromService(any(), any())
            verify(weatherSrcMock, times(1)).saveCurrent5DayForecastToLocal(any())
            verify(weatherSrcMock, times(5)).getCurrent5DayForecastFromLocal(any())
            assertEquals(forecastListMock, result)
        }
    }

    private fun notConnected() {
        whenever(configSrcMock.getNetworkStatus()).thenReturn(NetworkStatus.NOT_CONNECTED)
    }

    private fun connected() {
        whenever(configSrcMock.getNetworkStatus()).thenReturn(NetworkStatus.CONNECTED)
    }

    private suspend fun forecastSrcLocalEmpty() {
        whenever(weatherSrcMock.getCurrent5DayForecastFromLocal(any())).thenReturn(listOf())
    }

    private suspend fun forecastSrcLocalNotEmpty() {
        whenever(weatherSrcMock.getCurrent5DayForecastFromLocal(any())).thenReturn(forecastListMock)
    }

    private suspend fun weatherSrcLocalEmpty() {
        whenever(weatherSrcMock.getCurrentWeatherFromLocal(any())).thenReturn(null)
    }

    private suspend fun weatherSrcLocalNotEmpty() {
        whenever(weatherSrcMock.getCurrentWeatherFromLocal(any())).thenReturn(weatherEntityMock)
    }

    private suspend fun weatherSrcErrorFromService(exceptionClass: KClass<out Throwable>) {
        whenever(weatherSrcMock.getCurrentWeatherFromService(any(), any()))
                .thenThrow(createInstance(exceptionClass))
    }

    private suspend fun weatherSrcSuccessFromService() {
        whenever(weatherSrcMock.getCurrentWeatherFromService(any(), any()))
                .thenReturn(weatherEntityMock)
    }

    private suspend fun forecastSrcSuccessFromService() {
        whenever(weatherSrcMock.getCurrent5DayForecastFromService(any(),any()))
                .thenReturn(forecastListMock)
    }

    private fun thresholdExpired() {
        whenever(configSrcMock.getConfiguration()).thenReturn(
                Configuration(Units.SI, Long.MAX_VALUE)
        )
    }
    private fun thresholdNotExpired() {
        whenever(configSrcMock.getConfiguration()).thenReturn(
                Configuration(Units.SI, -1)
        )
    }
}