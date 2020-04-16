package accenturetest.com.weatherforecast.domain

interface ErrorHandler {

    fun getError(throwable: Throwable): ErrorEntity
}