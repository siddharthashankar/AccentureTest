package accenturetest.com.weatherforecast.framework.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import org.joda.time.DateTime
import java.lang.reflect.Type

class ForecastDeserializer : JsonDeserializer<ForecastEntity?> {

    override fun deserialize(json: JsonElement?,typeOfT: Type?, context: JsonDeserializationContext?): ForecastEntity? {
        json ?: return null
        val jsonObject = json.asJsonObject
        val weather = jsonObject["weather"].asJsonArray[0].asJsonObject
        val main = jsonObject["main"].asJsonObject
        return ForecastEntity(
                weather["id"].asInt,
                DateTime(jsonObject["dt"].asLong * 1000),
                main["temp"].asDouble,
                weather["main"].asString,
                weather["description"].asString,
                main["temp_min"].asDouble,
                main["temp_max"].asDouble
        )
    }
}