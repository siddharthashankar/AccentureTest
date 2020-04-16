package accenturetest.com.weatherforecast.data.config

import accenturetest.com.weatherforecast.domain.config.Configuration
import javax.inject.Inject

class ConfigurationRepository
@Inject constructor(private val configurationDataSource: ConfigurationDataSource) {

    fun getConfiguration(): Configuration = configurationDataSource.getConfiguration()
}