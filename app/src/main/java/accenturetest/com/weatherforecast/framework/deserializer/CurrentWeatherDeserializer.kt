package accenturetest.com.weatherforecast.framework.deserializer

import android.content.SharedPreferences
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import accenturetest.com.weatherforecast.util.Constants
import org.joda.time.DateTime
import java.lang.reflect.Type
import javax.inject.Inject

class CurrentWeatherDeserializer @Inject constructor(
        private val sharedPreferences: SharedPreferences
) : JsonDeserializer<WeatherEntity?> {

    override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
    ): WeatherEntity? {
        val jsonObject = json?.asJsonObject ?: return null
        val weatherObject = jsonObject["weather"].asJsonArray[0].asJsonObject
        val mainObject = jsonObject["main"].asJsonObject
        val locationObject = jsonObject["coord"].asJsonObject
        val coordinates = Coordinates(
                lon = locationObject["lon"].asDouble,
                lat = locationObject["lat"].asDouble
        )
        val units = sharedPreferences.getString(Constants.Keys.UNITS_KEY, Constants.Values.UNITS_SI)
        return WeatherEntity(
                weatherObject["id"].asInt,
                weatherObject["main"].asString,
                weatherObject["description"].asString,
                mainObject["temp"].asDouble,
                mainObject["temp_min"].asDouble,
                mainObject["temp_max"].asDouble,
                if (units == Constants.Values.UNITS_SI) Units.SI else Units.IMPERIAL,
                jsonObject["name"].asString,
                coordinates,
                DateTime.now(),
                mainObject["humidity"].asInt,
                jsonObject["wind"].asJsonObject["speed"].asDouble
        )
    }
}