package accenturetest.com.weatherforecast.di.module

import dagger.Binds
import dagger.Module
import accenturetest.com.weatherforecast.data.location.AppLocationRepository
import accenturetest.com.weatherforecast.data.location.LocationRepository
import accenturetest.com.weatherforecast.data.weather.AppWeatherRepository
import accenturetest.com.weatherforecast.data.weather.WeatherRepository
import javax.inject.Singleton

@Module
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun provideWeatherRepo(weather: AppWeatherRepository): WeatherRepository

    @Binds
    @Singleton
    abstract fun provideLocationRepo(locationRepo: AppLocationRepository): LocationRepository
}