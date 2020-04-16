
package accenturetest.com.weatherforecast.di.component

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import accenturetest.com.weatherforecast.di.module.AppModule
import accenturetest.com.weatherforecast.di.module.DataModule
import accenturetest.com.weatherforecast.di.module.FrameworkDataModule
import accenturetest.com.weatherforecast.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [FrameworkDataModule::class, DataModule::class, AppModule::class, ViewModelModule::class])
interface WeatherAppComponent {

    /* Exposed dependencies. */
    fun viewModelFactory(): ViewModelProvider.Factory

}