package accenturetest.com.weatherforecast.data.location

import accenturetest.com.weatherforecast.domain.Coordinates

interface LocationRepository {

    suspend fun getCurrentLocation(): Coordinates
}