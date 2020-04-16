package accenturetest.com.weatherforecast.interactors

import accenturetest.com.weatherforecast.data.config.ConfigurationRepository
import accenturetest.com.weatherforecast.domain.config.Configuration
import javax.inject.Inject

class GetConfiguration @Inject constructor(
        private val configurationRepository: ConfigurationRepository
) {
    operator fun invoke(): Configuration = configurationRepository.getConfiguration()
}