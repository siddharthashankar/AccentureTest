package accenturetest.com.weatherforecast.interactors

import accenturetest.com.weatherforecast.data.exceptions.AppErrorHandler
import accenturetest.com.weatherforecast.data.exceptions.LocationNullException
import accenturetest.com.weatherforecast.data.location.LocationRepository
import accenturetest.com.weatherforecast.data.weather.WeatherRepository
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.ErrorEntity
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import accenturetest.com.weatherforecast.util.Resource
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.io.IOException
import java.net.SocketTimeoutException

class GetCurrentWeatherWithLocationTest {

    private lateinit var SUT: GetCurrentWeatherWithLocation
    private lateinit var weatherRepoMock: WeatherRepository
    private lateinit var locationRepoMock: LocationRepository

    @Before
    fun setup() {
        weatherRepoMock = mock(WeatherRepository::class.java)
        locationRepoMock = mock(LocationRepository::class.java)
        SUT = GetCurrentWeatherWithLocation(weatherRepoMock, locationRepoMock, AppErrorHandler())
    }

    // getCurrentWeather fails location
    @Test
    fun `getCurrentWeather failsLocation locationErrorEntityExpected`() {
        runBlocking {
            `when`(locationRepoMock.getCurrentLocation()).thenAnswer { throw LocationNullException() }
            val value = SUT()
            assert(value is Resource.Error<WeatherEntity>)
            val errorEntity = value as Resource.Error<WeatherEntity>
            assert(errorEntity.errorEntity is ErrorEntity.LocationError)
            assert(errorEntity.errorEntity.originalException is LocationNullException)
        }
    }

    // getCurrentWeather fails call to web service
    @Test
    fun `getCurrentWeather failsCallToWebService serviceUnavailableErrorEntityExpected`() {
        runBlocking {
            locationSuccess()
            `when`(weatherRepoMock.getCurrentWeather(any(Coordinates::class.java))).thenAnswer { throw SocketTimeoutException() }
            val value = SUT()
            assert(value is Resource.Error<WeatherEntity>)
            val errorEntity = (value as Resource.Error<WeatherEntity>).errorEntity
            assert(errorEntity is ErrorEntity.ServiceUnavailable)
            assert(errorEntity.originalException is SocketTimeoutException || errorEntity.originalException is IllegalArgumentException)
        }
    }

    // getCurrentWeather network unavailable Network Error Entity expected
    @Test
    fun `getCurrentWeather notNetwork networkErrorEntityExpected`() {
        runBlocking {
            locationSuccess()
            `when`(weatherRepoMock.getCurrentWeather(any(Coordinates::class.java))).thenAnswer { throw IOException() }
            val value = SUT()
            assert(value is Resource.Error<WeatherEntity>)
            val errorEntity = (value as Resource.Error<WeatherEntity>).errorEntity
            assert(errorEntity is ErrorEntity.Network)
            Assert.assertTrue(errorEntity.originalException is IOException)
        }
    }
    // getCurrentWeather succeeds
    @Test
    fun `getCurrentWeather success weatherEntityExpected`() {
        runBlocking {
            locationSuccess()
            val expectedResult = WeatherEntity(1, "Clouds", "Description", 40.1, 20.2, 42.0, Units.SI, "Mexico", Coordinates(0.0, 0.0), DateTime.now(), 1, 1.0)
            `when`(weatherRepoMock.getCurrentWeather(any(Coordinates::class.java))).thenAnswer { expectedResult }
            val value = SUT()
            assert(value is Resource.Success<WeatherEntity>)
            Assert.assertNotNull((value as Resource.Success).data)
        }
    }

    private suspend fun locationSuccess() {
        `when`(locationRepoMock.getCurrentLocation()).thenAnswer { Coordinates(0.0, 0.0) }
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}