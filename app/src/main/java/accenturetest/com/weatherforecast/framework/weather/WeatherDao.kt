package accenturetest.com.weatherforecast.framework.weather

import androidx.room.*

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherRoomEntity)

    @Query("SELECT * FROM weathers WHERE locationId = :locationId")
    suspend fun getWeather(locationId: String): WeatherRoomEntity

    @Query("SELECT COUNT(*) FROM weathers WHERE locationId = :locationId")
    suspend fun getCount(locationId: String): Int
}