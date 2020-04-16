package accenturetest.com.weatherforecast.framework.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import accenturetest.com.weatherforecast.framework.weather.ForecastResponse
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import java.lang.reflect.Type

class SingleWeekForecastResponseDeserializer : JsonDeserializer<ForecastResponse> {

    override fun deserialize(
            json: JsonElement?,
            typeOfT: Type,
            context: JsonDeserializationContext
    ): ForecastResponse {
        json ?: return ForecastResponse(arrayListOf())
        val forecastList = json.asJsonObject["list"].asJsonArray
        val result = ArrayList<ForecastEntity>()
        for (forecastJson in forecastList) {
            val forecastEntity: ForecastEntity =
                    context.deserialize<ForecastEntity>(forecastJson, ForecastEntity::class.java)
            if (forecastEntity.date.hourOfDay == 12) {
                result.add(forecastEntity)
            }
        }
        return ForecastResponse(result)
    }
}