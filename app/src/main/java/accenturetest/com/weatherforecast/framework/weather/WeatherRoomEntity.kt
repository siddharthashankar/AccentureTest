package accenturetest.com.weatherforecast.framework.weather

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity

@Entity(tableName = "weathers")
data class WeatherRoomEntity(
        @PrimaryKey
        val locationId: String,
        @Embedded
        val weatherEntity: WeatherEntity
)