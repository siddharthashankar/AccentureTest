package accenturetest.com.weatherforecast.framework.location

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import accenturetest.com.weatherforecast.data.exceptions.LocationNullException
import accenturetest.com.weatherforecast.data.location.LocationDataSource
import accenturetest.com.weatherforecast.domain.Coordinates
import accenturetest.com.weatherforecast.util.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class AppLocationDataSource @Inject constructor(
        private val context: Context,
        @Named("internal_config")
        private val sharedPreferences: SharedPreferences
) : LocationDataSource {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastCoordinates: Coordinates
    private var lastUpdateTime: Long =
            sharedPreferences.getLong(Constants.Keys.LOCATION_LAST_UPDATE, System.currentTimeMillis())
    /* 30 minutes. */
    private val threshold = 3 * 10 * 60 * 1000

    // TODO: Save Location in SharedPrefs.
    override suspend fun getCurrent(): Coordinates {
        val timeDifference = System.currentTimeMillis() - lastUpdateTime
        if (timeDifference > threshold && this::lastCoordinates.isInitialized) {
            return lastCoordinates
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = fusedLocationClient.lastLocation.await() ?: throw LocationNullException()
        lastCoordinates = Coordinates(lon = location.longitude, lat = location.latitude)
        sharedPreferences.edit().apply {
            putLong(Constants.Keys.LOCATION_LAST_UPDATE, System.currentTimeMillis())
        }.apply()
        return lastCoordinates
    }
}