package accenturetest.com.weatherforecast.framework.weather

import androidx.room.*

@Dao
interface ForecastDao {

    @Insert
    suspend fun insert(forecastRoomEntity: ForecastRoomEntity)

    @Query("DELETE FROM forecasts WHERE type = :type AND locationId = :location")
    suspend fun deleteAllByTypeAndLocation(type: String, location: String)

    @Query("SELECT * FROM forecasts WHERE type = :type AND locationId = :location")
    suspend fun getByTypeAndLocation(type: String, location: String): List<ForecastRoomEntity>

    @Query("SELECT COUNT(*) FROM forecasts WHERE type = :type AND locationId = :location")
    suspend fun getCountByTypeAndLocation(type: String, location: String): Int
}