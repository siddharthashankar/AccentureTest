package accenturetest.com.weatherforecast

import android.app.Application
import accenturetest.com.weatherforecast.di.component.ActivityComponent
import accenturetest.com.weatherforecast.di.component.DaggerActivityComponent
import accenturetest.com.weatherforecast.di.component.DaggerWeatherAppComponent
import accenturetest.com.weatherforecast.di.component.WeatherAppComponent
import accenturetest.com.weatherforecast.di.module.FrameworkDataModule
import accenturetest.com.weatherforecast.di.module.AppModule
import net.danlew.android.joda.JodaTimeAndroid

class WeatherApp : Application() {

    private lateinit var mWeatherAppComponent: WeatherAppComponent
    private lateinit var mUiComponent: ActivityComponent

    override fun onCreate() {
        super.onCreate()
        mWeatherAppComponent = DaggerWeatherAppComponent.builder()
                .appModule(AppModule(this))
                .frameworkDataModule(FrameworkDataModule())
                .build()
        mUiComponent = DaggerActivityComponent.builder()
                .weatherAppComponent(mWeatherAppComponent)
                .build()
        JodaTimeAndroid.init(this)
    }

    fun getAppComponent(): WeatherAppComponent = mWeatherAppComponent

    fun getUiInjector(): ActivityComponent = mUiComponent
}