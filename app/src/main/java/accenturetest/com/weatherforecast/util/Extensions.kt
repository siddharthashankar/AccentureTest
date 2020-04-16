package accenturetest.com.weatherforecast.util

import accenturetest.com.weatherforecast.domain.ErrorHandler

suspend inline fun <T> tryOrHandle(errorHandler: ErrorHandler, body: ()->T): Resource<T> = try {
    Resource.Success(body())
} catch (throwable: Throwable) {
    throwable.printStackTrace()
    Resource.Error(errorHandler.getError(throwable))
}