package accenturetest.com.weatherforecast.framework.config

import android.content.SharedPreferences
import android.net.ConnectivityManager
import accenturetest.com.weatherforecast.data.config.ConfigurationDataSource
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.domain.config.Configuration
import accenturetest.com.weatherforecast.domain.config.NetworkStatus
import accenturetest.com.weatherforecast.util.Constants
import javax.inject.Inject

class AppConfigDataSource @Inject constructor(
        private val sharedPreferences: SharedPreferences,
        private val connectivityMgr: ConnectivityManager
) : ConfigurationDataSource {

    override fun getConfiguration(): Configuration {
        val savedUnits = sharedPreferences.getString(
                Constants.Keys.UNITS_KEY,
                Constants.Values.UNITS_SI
        )
        return Configuration(
                if (savedUnits == Constants.Values.UNITS_SI) Units.SI else Units.IMPERIAL,
                sharedPreferences.getLong(Constants.Keys.WEATHER_LAST_UPDATE, System.currentTimeMillis())
        )
    }

    override fun saveLastUpdate(lastUpdate: Long) {
        sharedPreferences.edit().putLong(Constants.Keys.WEATHER_LAST_UPDATE, lastUpdate)
    }

    override fun getNetworkStatus(): NetworkStatus {
        val activeNetworkInfo = connectivityMgr.activeNetworkInfo
        return if (activeNetworkInfo == null) {
            NetworkStatus.NOT_CONNECTED
        } else {
            val isConnected = activeNetworkInfo.isConnected
            if (isConnected) NetworkStatus.CONNECTED else NetworkStatus.NOT_CONNECTED
        }
    }
}