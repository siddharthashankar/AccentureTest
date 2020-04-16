package accenturetest.com.weatherforecast.data.exceptions

import accenturetest.com.weatherforecast.domain.ErrorEntity
import accenturetest.com.weatherforecast.domain.ErrorHandler
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class AppErrorHandler : ErrorHandler {

    override fun getError(throwable: Throwable): ErrorEntity = when (throwable) {
        is ConnectivityException -> ErrorEntity.Network(throwable)
        is LocationNullException -> ErrorEntity.LocationError(throwable)
        is HttpException -> ErrorEntity.Network(throwable)
        is SocketTimeoutException -> ErrorEntity.ServiceUnavailable(throwable)
        is IOException -> ErrorEntity.Network(throwable)
        is IllegalArgumentException -> ErrorEntity.ServiceUnavailable(throwable)
        is IllegalStateException -> ErrorEntity.ServiceUnavailable(throwable)
        else -> ErrorEntity.Unknown(throwable)
    }
}