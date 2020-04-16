package accenturetest.com.weatherforecast.framework

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import accenturetest.com.weatherforecast.framework.weather.ForecastDao
import accenturetest.com.weatherforecast.framework.weather.ForecastRoomEntity
import accenturetest.com.weatherforecast.framework.weather.WeatherDao
import accenturetest.com.weatherforecast.framework.weather.WeatherRoomEntity

@Database(entities = [WeatherRoomEntity::class, ForecastRoomEntity::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    abstract fun forecastDao(): ForecastDao
}