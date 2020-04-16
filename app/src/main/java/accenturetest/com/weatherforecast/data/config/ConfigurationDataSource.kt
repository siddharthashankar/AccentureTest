package accenturetest.com.weatherforecast.data.config

import accenturetest.com.weatherforecast.domain.config.Configuration
import accenturetest.com.weatherforecast.domain.config.NetworkStatus

interface ConfigurationDataSource {

    fun getConfiguration(): Configuration

    fun saveLastUpdate(lastUpdate: Long)

    fun getNetworkStatus(): NetworkStatus
}