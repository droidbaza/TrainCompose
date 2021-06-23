package com.droidbaza.traincompose.components.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {

    val location = MutableStateFlow<Location>(getInitialLocation())
    val clicked = MutableStateFlow<String>("")
    val addressText = mutableStateOf("")
    var isMapEditable = mutableStateOf(true)
    var timer: CountDownTimer? = null

    fun getInitialLocation(): Location {
        val initialLocation = Location("")
        initialLocation.latitude = 51.506874
        initialLocation.longitude = -0.139800
        return initialLocation
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        if (latitude != location.value.latitude) {
            val location = Location("")
            location.latitude = latitude
            location.longitude = longitude
            setLocation(location)
        }
    }

    fun setLocation(loc: Location) {
        location.value = loc
    }

    fun getAddressFromLocation(context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        val address: Address?
        var addressText = ""

        try {
            addresses =
                geocoder.getFromLocation(location.value.latitude, location.value.longitude, 1)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        address = addresses?.get(0)
        addressText = address?.getAddressLine(0) ?: ""


        return addressText
    }

    fun onTextChanged(context: Context, text: String) {
        if (text == "")
            return
        timer?.cancel()
        timer = object : CountDownTimer(1000, 1500) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                location.value = getLocationFromAddress(context, text)
            }
        }.start()
    }

    fun getLocationFromAddress(context: Context, strAddress: String): Location {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?

        addresses = geocoder.getFromLocationName(strAddress, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]

            var loc = Location("")
            loc.latitude = address.getLatitude()
            loc.longitude = address.getLongitude()
            return loc
        }

        return location.value
    }

    fun markerClicked(toString: String) {
        Log.d("MMMMM", "$toString")
        clicked.value = toString
    }

}