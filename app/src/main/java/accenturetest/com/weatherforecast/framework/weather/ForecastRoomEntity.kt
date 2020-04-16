package accenturetest.com.weatherforecast.framework.weather

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.domain.forecast.ForecastType

@Entity(tableName = "forecasts")
data class ForecastRoomEntity(
        @PrimaryKey(autoGenerate = true)
        var forecastId: Int?,
        val locationId: String,
        @Embedded
        val forecastEntity: ForecastEntity,
        val type: ForecastType
)