package accenturetest.com.weatherforecast.data.location

import accenturetest.com.weatherforecast.domain.Coordinates
import javax.inject.Inject

class AppLocationRepository @Inject constructor(
        private val locationDataSource: LocationDataSource
) : LocationRepository {

    override suspend fun getCurrentLocation(): Coordinates = locationDataSource.getCurrent()
}