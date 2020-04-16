package accenturetest.com.weatherforecast.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import accenturetest.com.weatherforecast.domain.config.Configuration
import accenturetest.com.weatherforecast.interactors.GetConfiguration
import accenturetest.com.weatherforecast.interactors.GetCurrentWeatherWithLocation
import accenturetest.com.weatherforecast.interactors.GetOneWeekForecast
import accenturetest.com.weatherforecast.model.Forecast
import accenturetest.com.weatherforecast.model.Weather
import accenturetest.com.weatherforecast.model.transformation.Transformers
import accenturetest.com.weatherforecast.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val getCurrentWeatherWithLocation: GetCurrentWeatherWithLocation,
        private val getOneWeekForecast: GetOneWeekForecast,
        private val getConfiguration: GetConfiguration
) : ViewModel() {

    val currentWeatherLiveData: LiveData<Resource<Weather>> = MutableLiveData()
    val forecastLiveData: LiveData<Resource<List<Forecast>>> = MutableLiveData()

    fun getWeatherAtCurrentLocation() = viewModelScope.launch(Dispatchers.IO) {
        val weatherResource = getCurrentWeatherWithLocation()
        when (weatherResource) {
            is Resource.Success -> {
                val resourceValue = Transformers.fromDomainWeather(weatherResource.data)
                (currentWeatherLiveData as MutableLiveData).postValue(Resource.Success(resourceValue))
            }
            is Resource.Error -> {
                val resource = Resource.Error<Weather>(weatherResource.errorEntity)
                (currentWeatherLiveData as MutableLiveData).postValue(resource)
            }
        }
    }

    fun fetchOneWeekForecast() = viewModelScope.launch(Dispatchers.IO) {
        val oneWeekForecastResource = getOneWeekForecast()
        when (oneWeekForecastResource) {
            is Resource.Success -> {
                val forecastList =
                        oneWeekForecastResource.data.map { Transformers.fromDomainForecast(it) }
                (forecastLiveData as MutableLiveData).postValue(Resource.Success(forecastList))
            }
            is Resource.Error -> {
                val errorResource =
                        Resource.Error<List<Forecast>>(oneWeekForecastResource.errorEntity)
                (forecastLiveData as MutableLiveData).postValue(errorResource)
            }
        }
    }

    fun getConfig(): Configuration = getConfiguration()
}