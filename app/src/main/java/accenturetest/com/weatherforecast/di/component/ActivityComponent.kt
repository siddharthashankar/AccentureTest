package accenturetest.com.weatherforecast.di.component

import dagger.Component
import accenturetest.com.weatherforecast.di.ActivityScope
import accenturetest.com.weatherforecast.ui.main.MainActivity

@ActivityScope
@Component(dependencies = [WeatherAppComponent::class])
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)
}