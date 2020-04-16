package accenturetest.com.weatherforecast.di.module
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import accenturetest.com.weatherforecast.data.location.LocationDataSource
import accenturetest.com.weatherforecast.data.weather.WeatherDataSource
import accenturetest.com.weatherforecast.domain.weather.WeatherEntity
import dagger.Module
import dagger.Provides
import accenturetest.com.weatherforecast.data.config.ConfigurationDataSource
import accenturetest.com.weatherforecast.framework.weather.ForecastResponse
import accenturetest.com.weatherforecast.domain.forecast.ForecastEntity
import accenturetest.com.weatherforecast.framework.AppDatabase
import accenturetest.com.weatherforecast.framework.weather.WeatherService
import accenturetest.com.weatherforecast.framework.config.AppConfigDataSource
import accenturetest.com.weatherforecast.framework.location.AppLocationDataSource
import accenturetest.com.weatherforecast.framework.weather.AppWeatherDataSource
import accenturetest.com.weatherforecast.framework.deserializer.CurrentWeatherDeserializer
import accenturetest.com.weatherforecast.framework.deserializer.ForecastDeserializer
import accenturetest.com.weatherforecast.framework.deserializer.SingleWeekForecastResponseDeserializer
import accenturetest.com.weatherforecast.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [AppModule::class])
class FrameworkDataModule {

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    @Singleton
    fun provideGson(
            currentWeatherDeserializer: CurrentWeatherDeserializer,
            forecastDeserializer: ForecastDeserializer,
            singleWeekForecastDeserializer: SingleWeekForecastResponseDeserializer
    ): Gson = GsonBuilder()
                .registerTypeAdapter(WeatherEntity::class.java, currentWeatherDeserializer)
                .registerTypeAdapter(ForecastEntity::class.java, forecastDeserializer)
                .registerTypeAdapter(ForecastResponse::class.java, singleWeekForecastDeserializer)
                .create()

    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationSource(
            appContext: Context,
            @Named("internal_config") sharedPreferences: SharedPreferences
    ): LocationDataSource = AppLocationDataSource(appContext, sharedPreferences)


    @Provides
    @Singleton
    fun provideWeatherSource(
            weatherService: WeatherService,
            appDatabase: AppDatabase
    ): WeatherDataSource = AppWeatherDataSource(
            weatherService,
            appDatabase
    )

    @Provides
    @Singleton
    fun providePreferencesSource(
            sharedPreferences: SharedPreferences,
            connectivityManager: ConnectivityManager
    ): ConfigurationDataSource =
            AppConfigDataSource(sharedPreferences, connectivityManager)

    @Provides
    @Singleton
    fun provideForecastDeserializer(): ForecastDeserializer = ForecastDeserializer()

    @Provides
    @Singleton
    fun provideOneWeekForecastDeserializer(): SingleWeekForecastResponseDeserializer =
            SingleWeekForecastResponseDeserializer()
}