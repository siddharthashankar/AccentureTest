package accenturetest.com.weatherforecast.util

import accenturetest.com.weatherforecast.domain.ErrorEntity

sealed class Resource<T> {

    data class Success<T>(val data: T): Resource<T>()

    data class Error<T>(val errorEntity: ErrorEntity): Resource<T>()
}