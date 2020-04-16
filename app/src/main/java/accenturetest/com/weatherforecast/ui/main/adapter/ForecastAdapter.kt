package accenturetest.com.weatherforecast.ui.main.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import accenturetest.com.weatherforecast.databinding.ForecastItemBinding
import accenturetest.com.weatherforecast.domain.Units
import accenturetest.com.weatherforecast.model.Forecast
import accenturetest.com.weatherforecast.util.ActivityUtils
import kotlinx.android.synthetic.main.forecast_item.view.*

class ForecastAdapter(private val list: List<Forecast>, var units: Units) :
        RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(
            private val binding: ForecastItemBinding
    ) :RecyclerView.ViewHolder(binding.root) {

        fun bindData(forecast: Forecast) {
            binding.apply {
                this.forecast = forecast
                this.units = this@ForecastAdapter.units
                root.forecast_item_icon.setImageResource(ActivityUtils.getIconRes(forecast.id))
            }.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ForecastItemBinding.inflate(layoutInflater, parent, false)
        return ForecastViewHolder(binding)
    }
}




