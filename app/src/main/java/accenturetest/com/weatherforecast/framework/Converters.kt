package accenturetest.com.weatherforecast.framework

import androidx.room.TypeConverter
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.domain.forecast.ForecastType
import org.joda.time.DateTime

/**
 * Class for defining converters for storing complex data types.
 */
class Converters {

    @TypeConverter
    fun unitsToString(units: Units): String = units.value

    @TypeConverter
    fun stringToUnits(value: String): Units = if (value == Units.IMPERIAL.value) {
        Units.IMPERIAL
    } else {
        Units.SI
    }

    @TypeConverter
    fun coordinatesToString(coordinates: Coordinates) = "${coordinates.lon},${coordinates.lat}"

    @TypeConverter
    fun stringToCoordinates(value: String): Coordinates {
        val values = value.split(",")
        return Coordinates(lon = values[0].toDouble(), lat = values[1].toDouble())
    }

    @TypeConverter
    fun forecastTypeToString(forecastType: ForecastType): String = forecastType.value

    @TypeConverter
    fun stringToForecastType(value: String): ForecastType = when (value) {
        ForecastType.FIVE_DAYS.value -> ForecastType.FIVE_DAYS
        else -> ForecastType.SIXTEEN_DAYS
    }

    @TypeConverter
    fun dateTimeToLong(dateTime: DateTime): Long = dateTime.millis

    @TypeConverter
    fun longToDateTime(value: Long): DateTime = DateTime(value)
}