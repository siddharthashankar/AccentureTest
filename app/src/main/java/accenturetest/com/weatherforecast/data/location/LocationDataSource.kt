package accenturetest.com.weatherforecast.data.location

import accenturetest.com.weatherforecast.domain.Coordinates

interface LocationDataSource {

    suspend fun getCurrent(): Coordinates
}