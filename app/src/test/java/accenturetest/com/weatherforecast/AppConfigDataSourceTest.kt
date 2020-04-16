package accenturetest.com.weatherforecast

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.nhaarman.mockitokotlin2.*
import accenturetest.com.weatherforecast.domain.config.NetworkStatus
import accenturetest.com.weatherforecast.framework.config.AppConfigDataSource
import accenturetest.com.weatherforecast.util.Constants
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class AppConfigDataSourceTest {

    private lateinit var SUT: AppConfigDataSource
    private lateinit var connectivityMgrMock: ConnectivityManager
    private lateinit var sharedPrefsMock: SharedPreferences
    private lateinit var networkInfoMock: NetworkInfo

    @Before
    fun setup() {
        connectivityMgrMock = mock()
        sharedPrefsMock = mock()
        networkInfoMock = mock()

        SUT = AppConfigDataSource(sharedPrefsMock, connectivityMgrMock)
    }

    @Test
    fun getConfiguration() {
        argumentCaptor<String> {
            SUT.getConfiguration()
            verify(sharedPrefsMock, times(1)).getString(capture(), capture())
            assertEquals(Constants.Keys.UNITS_KEY, firstValue)
            assertEquals(Constants.Values.UNITS_SI, secondValue)
            verify(sharedPrefsMock, times(1)).getLong(capture(), any())
            assertEquals(Constants.Keys.WEATHER_LAST_UPDATE, thirdValue)
        }
    }

    @Test
    fun getNetworkStatus() {
        networkInfoNull()
        var networkStatus = SUT.getNetworkStatus()
        verify(connectivityMgrMock, times(1)).activeNetworkInfo
        verify(networkInfoMock, times(0)).isConnected
        assertEquals(NetworkStatus.NOT_CONNECTED, networkStatus)
        networkInfoNotNull()
        notConnected()
        networkStatus = SUT.getNetworkStatus()
        verify(connectivityMgrMock, times(2)).activeNetworkInfo
        verify(networkInfoMock, times(1)).isConnected
        assertEquals(NetworkStatus.NOT_CONNECTED, networkStatus)
        connected()
        networkStatus = SUT.getNetworkStatus()
        verify(connectivityMgrMock, times(3)).activeNetworkInfo
        verify(networkInfoMock, times(2)).isConnected
        assertEquals(NetworkStatus.CONNECTED, networkStatus)
    }

    private fun networkInfoNull() {
        whenever(connectivityMgrMock.activeNetworkInfo).thenReturn(null)
    }

    private fun networkInfoNotNull() {
        whenever(connectivityMgrMock.activeNetworkInfo).thenReturn(networkInfoMock)
    }

    private fun notConnected() {
        whenever(networkInfoMock.isConnected)
                .thenReturn(false)
    }

    private fun connected() {
        whenever(networkInfoMock.isConnected).thenReturn(true)
    }
}