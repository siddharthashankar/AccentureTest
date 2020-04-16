package accenturetest.com.weatherforecast.ui.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import accenturetest.com.weatherforecast.R
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.WeatherApp
import accenturetest.com.weatherforecast.databinding.ActivityMainBinding
import accenturetest.com.weatherforecast.domain.ErrorEntity
import accenturetest.com.weatherforecast.domain.config.Configuration
import accenturetest.com.weatherforecast.model.Forecast
import accenturetest.com.weatherforecast.model.Weather
import accenturetest.com.weatherforecast.ui.main.adapter.ForecastAdapter
import accenturetest.com.weatherforecast.util.Resource
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.Period
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        @BindingAdapter("lastUpdate")
        fun TextView.bindLastUpdate(dateTime: DateTime?) {
            if (dateTime == null) {
                text = ""
                return
            }
            val period = Period(dateTime, DateTime.now())
            val template = context.getString(R.string.last_update_template)
            val timePart = when {
                period.days == 1 -> "${period.days} ${context.getString(R.string.day)}"
                period.hours == 1 -> "${period.hours} ${context.getString(R.string.hour)}"
                period.minutes == 1 -> "${period.minutes} ${context.getString(R.string.minute)}"
                period.days > 0 -> "${period.days} ${context.getString(R.string.days)}"
                period.hours > 0 -> "${period.hours} ${context.getString(R.string.hours)}"
                period.minutes > 0 -> "${period.minutes} ${context.getString(R.string.minutes)}"
                else -> context.getString(R.string.moments_ago)
            }
            text = String.format(template, timePart)
        }
    }


    @Inject
    lateinit var mFactory: ViewModelProvider.Factory
    private lateinit var mViewModel: MainViewModel
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mForecastList: ArrayList<Forecast>
    private lateinit var mForecastAdapter: ForecastAdapter
    private lateinit var mConfiguration: Configuration
    private var isPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        inject()
        bindUi()
        askLocationPermission()
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
    }

    override fun onResume() {
        super.onResume()
        mBinding.invalidateAll()
    }

    private fun inject() {
        (application as WeatherApp).getUiInjector().inject(this)
        mViewModel = ViewModelProvider(this, mFactory)[MainViewModel::class.java]
    }

    private fun bindUi() {
        mForecastList = ArrayList()
        mConfiguration = mViewModel.getConfig()
        mForecastAdapter = ForecastAdapter(mForecastList, mConfiguration.defaultUnits)
        mBinding.isLoading = true
        /*rvForecast.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            adapter = mForecastAdapter
        }*/
        mViewModel.currentWeatherLiveData.observe(this, Observer { weatherResource ->
            showWeather(weatherResource)
            mBinding.isLoading = false
        })
        mViewModel.forecastLiveData.observe(this, Observer { forecastResource ->
            showForecast(forecastResource)
            mBinding.isLoading = false
        })
    }

    private fun askLocationPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        mBinding.isLoading = true
                        mViewModel.getWeatherAtCurrentLocation()
                        mViewModel.fetchOneWeekForecast()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?
                    ) = Unit

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        AlertDialog.Builder(this@MainActivity)
                                .setTitle(R.string.main_location_permission_title)
                                .setMessage(R.string.main_location_permission)
                                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    askLocationPermission()
                                }
                    }
                }).check()
    }

    private fun showWeather(resource: Resource<Weather>) {
        when (resource) {
            is Resource.Success -> {
                val weather = resource.data
                val units = if (weather.units == Units.SI) "C" else "F"
                mBinding.apply {
                    this.units = units
                    this.weather = weather
                    this.lastUpdate = weather.lastUpdate
                }
            }
            is Resource.Error -> {
                showErrorSnackbar(resource.errorEntity)
            }
        }
    }

    private fun showForecast(forecastResource: Resource<List<Forecast>>) {
        when (forecastResource) {
            is Resource.Success -> {
                mForecastList.clear()
                mForecastList.addAll(forecastResource.data)
                mForecastAdapter.notifyDataSetChanged()
            }
            is Resource.Error -> {
                // TODO Show forecast error text.
                // TODO Handle real error.
                showErrorSnackbar(ErrorEntity.Unknown(forecastResource.errorEntity.originalException))
            }
        }
    }

    // TODO: Add error handling.
    private fun showErrorSnackbar(error: ErrorEntity) {
        if (error is ErrorEntity.Network || error is ErrorEntity.ServiceUnavailable) {
            Snackbar.make(
                    mainContainer,
                    R.string.main_weather_error,
                    Snackbar.LENGTH_SHORT
            ).show()
        } else {
            Snackbar.make(
                    mainContainer,
                    R.string.generic_error,
                    Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
