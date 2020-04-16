package accenturetest.com.weatherforecast.util

/**
 * Created by jcasas on 8/10/17.
 */
object Constants {

    object Keys {
        const val GLOBAL_PREFS_NAME: String = "global_prefs"
        const val UNITS_KEY: String = "units"
        const val LOCATION_LAST_UPDATE: String = "location_last_update"
        const val WEATHER_LAST_UPDATE: String = "weather_last_update"
    }

    object Values {
        const val UNITS_SI: String = "si"
        const val UNITS_IMPERIAL: String = "imperial"
    }

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    const val API_KEY = "9802399d085d7d8d57eb8e4608133ec7"//"700ec11b01c5c28b59d8087a038c09c2"

    const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    const val INTERNAL_CONFIG_PREFS: String = "internal_config_prefs"

    const val DATABASE_NAME: String = "app_database"
}