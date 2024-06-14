package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtils(private val context: Context) {
    // This is main entry point for using the Google Maps API
    private val _fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(locationViewModel: LocationViewModel) {  // For updating location
        // locationCallback is used in a way that when we request something it gets finalized and then is passed to us
        val locationCallback = object : LocationCallback() {    // Instead of inheriting classes, we can directly make objects using this line of code, so now locationCallback is an object of LocationCallback() class
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(it.latitude, it.longitude)
                    locationViewModel.updateLocation(location)
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        _fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // Finding out the address from the location obtained by requestLocationUpdates
    fun reverseGeocodeLocation(location: LocationData): String {
        val geocoder = Geocoder(context, Locale.getDefault())   // Geocoder is used to give us address in a certain format, it is used to obtain address from the coordinates, Locale gives us format of the address(getDefault() means the system default)
        val coordinates = LatLng(location.latitude, location.longitude)     // We obtain coordinates by using the latitude and longitude
        val addresses: MutableList<Address>? = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)     // This provides us with a list of addresses decoded by geocoder using the coordinates

        return if(addresses?.isNotEmpty() == true) {    // Returning the address
            addresses[0].getAddressLine(0)        // Returning only the first line of address from list of addresses given by the geocoder
        } else {
            "Address Not Available"
        }
    }

    fun hasLocationPermission(context: Context): Boolean {      // This checks if the user has given location permission or not
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}