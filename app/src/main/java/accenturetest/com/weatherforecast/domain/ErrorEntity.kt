package accenturetest.com.weatherforecast.domain

sealed class ErrorEntity {

    abstract val originalException: Throwable

    data class Network(override val originalException: Throwable) : ErrorEntity()

    data class LocationError(override val originalException: Throwable): ErrorEntity()

    data class ServiceUnavailable(override val originalException: Throwable): ErrorEntity()

    data class Unknown(override val originalException: Throwable): ErrorEntity()
}