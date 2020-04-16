package accenturetest.com.weatherforecast.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import accenturetest.com.weatherforecast.R

/**
 * Created by jcasas on 8/12/17.
 */
object ActivityUtils {

    fun createStandardAlert(title: Int, message: Int, context: Context): AlertDialog {
        return AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.accept_button_string) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .create()
    }

    fun getStringByRes(stringId: Int, context: Context): String {
        return context.getString(stringId)
    }

    fun getIconRes(id: Int): Int = when (id) {
        in 200..232 -> R.drawable.ic_storm
        in 300..321 -> R.drawable.ic_raining
        in 300..321 -> R.drawable.ic_raining
        in 500..504 -> R.drawable.ic_light_rain
        511 -> R.drawable.ic_hail
        in 520..531 -> R.drawable.ic_raining
        // TODO Change for Snow
        in 600..622 -> R.drawable.ic_cloud
        //
        in 701..781 -> R.drawable.ic_cloud
        800 -> R.drawable.ic_sun
        801 -> R.drawable.ic_cloud
        in 802..804 -> R.drawable.ic_cloud
        else -> 0
    }
}