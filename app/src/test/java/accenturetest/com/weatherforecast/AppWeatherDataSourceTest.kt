package accenturetest.com.weatherforecast

import com.nhaarman.mockitokotlin2.*
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.domain.forecast.ForecastType
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import accenturetest.com.weatherforecast.framework.AppDatabase
import accenturetest.com.weatherforecast.framework.weather.*
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatchers.anyString
import java.io.IOException

class AppWeatherDataSourceTest {

    private lateinit var SUT: AppWeatherDataSource
    private lateinit var weatherServiceMock: WeatherService
    private lateinit var appDbMock: AppDatabase
    private lateinit var weatherDaoMock: WeatherDao
    private lateinit var forecastDao: ForecastDao
    private lateinit var weatherEntityMock: WeatherEntity
    private lateinit var forecastListMock: MutableList<ForecastEntity>
    private lateinit var coordinatesMock: Coordinates

    @Before
    fun setup() {
        weatherServiceMock = mock()
        appDbMock = mock()
        weatherDaoMock = mock()
        forecastDao = mock()
        whenever(appDbMock.weatherDao()).thenAnswer { weatherDaoMock }
        whenever(appDbMock.forecastDao()).thenAnswer { forecastDao }
        coordinatesMock = Coordinates(1.0, -1.0)
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
        SUT = AppWeatherDataSource(
                weatherServiceMock,
                appDbMock
        )
    }

    @Test(expected = Exception::class)
    fun getCurrentFromServiceTest() {
        runBlocking {
            try {
                noInternet()
                SUT.getCurrentWeatherFromService(Coordinates(10.099, -12.0122), Units.SI)
            } catch (exception: Exception) {
                assert(exception is IOException)
            }
            successWeatherService()
            argumentCaptor<String> {
                val result = SUT.getCurrentWeatherFromService(Coordinates(10.099, -12.0112), Units.SI)
                verify(weatherServiceMock, times(1)).getCurrentWeather(any(), any(), any(), capture())
                assert(firstValue == Units.SI.value)
                assertEquals(result, weatherEntityMock)
            }
            successWeatherService()
            argumentCaptor<String> {
                val result = SUT.getCurrentWeatherFromService(Coordinates(10.099, -12.0112), Units.IMPERIAL)
                verify(weatherServiceMock, times(1)).getCurrentWeather(any(), any(), any(), capture())
                assert(firstValue == Units.IMPERIAL.value)
                assertEquals(result, weatherEntityMock)
            }
            try {
                parsingErrorWeatherService()
                SUT.getCurrentWeatherFromService(coordinatesMock, Units.SI)
            } catch (exception: Exception) {
                assert(exception is IllegalStateException)
            }
            try {
                unknownErrorWeatherService()
                SUT.getCurrentWeatherFromService(coordinatesMock, Units.SI)
            } catch (exception: Exception) {
                assert(true)
            }
        }
    }

    @Test(expected = Exception::class)
    fun getCurrentFromLocalTest() = runBlocking {
        emptyWeatherDb()
        argumentCaptor<String> {
            val result = SUT.getCurrentWeatherFromLocal(Units.SI)
            verify(weatherDaoMock, times(1)).getCount(capture())
            assertEquals("current", firstValue)
            assertNull(result)
        }
        argumentCaptor<String> {
            val result = SUT.getCurrentWeatherFromLocal(Units.SI)
            verify(weatherDaoMock, times(1)).getWeather(capture())
            assertEquals("current", firstValue)
            assertNull(result)
        }
        insertAndReturnWeatherEntity()
        val result = SUT.getCurrentWeatherFromLocal(Units.SI)
        assertEquals(weatherEntityMock, result)
    }

    @Test
    fun saveCurrentWeatherToLocalTest() {
        runBlocking {
            argumentCaptor<WeatherRoomEntity> {
                SUT.saveCurrentWeatherToLocal(weatherEntityMock)
                verify(weatherDaoMock, times(1)).insertWeather(capture())
                assertEquals(WeatherRoomEntity("current", weatherEntityMock), firstValue)
            }
        }
    }

    @Test
    fun getForecastFromService() {
        runBlocking {
            successWeatherService()
            argumentCaptor<String> {
                SUT.getCurrent5DayForecastFromService(coordinatesMock, Units.SI)
                verify(weatherServiceMock, times(1)).get5dayForecast(any(), any(), any(), capture())
                assertEquals(Units.SI.value, firstValue)
            }
            successWeatherService()
            argumentCaptor<String> {
                SUT.getCurrent5DayForecastFromService(coordinatesMock, Units.IMPERIAL)
                verify(weatherServiceMock, times(2)).get5dayForecast(any(), any(), any(), capture())
                assertEquals(Units.IMPERIAL.value, secondValue)
            }
            successWeatherService()
            val result = SUT.getCurrent5DayForecastFromService(coordinatesMock, Units.SI)
            assertEquals(forecastListMock, result)
            try {
                parsingErrorWeatherService()
                SUT.getCurrent5DayForecastFromService(coordinatesMock, Units.SI)
            } catch (exception: Exception) {
                assert(exception is IllegalStateException)
            }
            try {
                unknownErrorWeatherService()
                SUT.getCurrent5DayForecastFromService(coordinatesMock, Units.SI)
            } catch (exception: Exception) {
                assert(true)
            }
        }
    }

    @Test
    fun saveCurrent5DayForecastToLocal() {
        runBlocking {
            argumentCaptor<String> {
                SUT.saveCurrent5DayForecastToLocal(forecastListMock)
                verify(forecastDao, times(1))
                        .deleteAllByTypeAndLocation(capture(), capture())
                assertEquals(ForecastType.FIVE_DAYS.value, firstValue)
                assertEquals("current", secondValue)
            }
            argumentCaptor<ForecastRoomEntity> {
                for (i in 0..100) {
                    forecastListMock.add(ForecastEntity(
                            i,
                            DateTime(),
                            1.1,
                            "",
                            "", 1.0, -1.0
                    ))
                }
                SUT.saveCurrent5DayForecastToLocal(forecastListMock)
                verify(forecastDao, times(forecastListMock.size+1)).insert(capture())
                val arguments = allValues.subList(1, allValues.size)
                for ((index, addedForecast) in forecastListMock.withIndex()) {
                    val addedForecastRoomEntity = ForecastRoomEntity(
                            null,
                            "current",
                            addedForecast,
                            ForecastType.FIVE_DAYS
                    )
                    assertEquals(addedForecastRoomEntity.forecastId, arguments[index].forecastId)
                    assertEquals(addedForecastRoomEntity.locationId, arguments[index].locationId)
                    assertEquals(addedForecastRoomEntity.forecastEntity, arguments[index].forecastEntity)
                    assertEquals(addedForecastRoomEntity.type, arguments[index].type)
                }
            }
        }
    }

    @Test
    fun getCurrent5DayForecastFromLocalTest() {
        runBlocking {
            argumentCaptor<String> {
                emptyForecastDb()
                val result = SUT.getCurrent5DayForecastFromLocal(Units.SI)
                verify(forecastDao, times(1)).getCountByTypeAndLocation(capture(), capture())
                assertEquals(ForecastType.FIVE_DAYS.value, firstValue)
                assertEquals("current", secondValue)
                assertEquals(listOf<ForecastEntity>(), result)
            }
            argumentCaptor<String> {
                forecastNotEmpty()
                val result = SUT.getCurrent5DayForecastFromLocal(Units.SI)
                verify(forecastDao, times(1)).getByTypeAndLocation(capture(), capture())
                assertEquals(ForecastType.FIVE_DAYS.value, firstValue)
                assertEquals("current", secondValue)
                assertEquals(forecastListMock, result)
            }
        }
    }

    private suspend fun successWeatherService() {
        whenever(weatherServiceMock.getCurrentWeather(
                any(),
                any(),
                anyString(),
                anyString()
        )).thenAnswer { weatherEntityMock }
        whenever(weatherServiceMock.get5dayForecast(
                any(),
                any(),
                anyString(),
                anyString()
        )).thenAnswer { ForecastResponse(forecastListMock) }
    }

    private suspend fun noInternet() {
        whenever(weatherServiceMock.getCurrentWeather(
                any(),
                any(),
                anyString(),
                anyString()
        )).thenAnswer { throw IOException() }
        whenever(weatherServiceMock.get5dayForecast(
                any(),
                any(),
                anyString(),
                anyString()
        )).thenAnswer { throw IOException() }
    }

    private suspend fun parsingErrorWeatherService() {
        whenever(weatherServiceMock.getCurrentWeather(
                any(),
                any(),
                any(),
                anyString()
        )).thenAnswer { throw IllegalStateException() }
        whenever(weatherServiceMock.get5dayForecast(
                any(),
                any(),
                any(),
                anyString()
        )).thenAnswer { throw IllegalStateException() }
    }

    private suspend fun unknownErrorWeatherService() {
        whenever(weatherServiceMock.getCurrentWeather(
                any(),
                any(),
                any(),
                anyString()
        )).thenAnswer { throw Exception() }
        whenever(weatherServiceMock.get5dayForecast(
                any(),
                any(),
                any(),
                anyString()
        )).thenAnswer { throw Exception() }
    }

    private suspend fun insertAndReturnWeatherEntity() {
        whenever(weatherDaoMock.insertWeather(any()))
                .then { runBlocking {
                    whenever(weatherDaoMock.getWeather(anyString()))
                            .thenAnswer { WeatherRoomEntity("current", weatherEntityMock) }
                    }
                }
    }

    private suspend fun forecastNotEmpty() {
        whenever(forecastDao.getByTypeAndLocation(anyString(), eq("current")))
                .thenAnswer { weatherEntityMock }
        whenever(forecastDao.getCountByTypeAndLocation(anyString(), eq("current")))
                .thenAnswer { 1 }
        whenever(forecastDao.getByTypeAndLocation(anyString(), eq("current")))
                .thenAnswer { forecastListMock.map { ForecastRoomEntity(
                        null,
                        "current",
                        it,
                        ForecastType.FIVE_DAYS
                ) } }
    }

    private suspend fun emptyWeatherDb() {
        whenever(weatherDaoMock.getWeather(anyString()))
                .thenAnswer { null }
    }

    private suspend fun emptyForecastDb() {
        whenever(forecastDao.getByTypeAndLocation(anyString(), anyString()))
                .thenAnswer { listOf<ForecastEntity>() }
        whenever(forecastDao.getCountByTypeAndLocation(anyString(), anyString()))
                .thenAnswer { 0 }
    }
}