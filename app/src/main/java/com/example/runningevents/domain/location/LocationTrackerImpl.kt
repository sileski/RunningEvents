package com.example.runningevents.domain.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationTrackerImpl(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getUserCurrentLocation(): Location? {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

        if (!hasFineLocationPermission || !hasCoarseLocationPermission || !isGpsEnabled) {
            return null
        }

        return suspendCancellableCoroutine { coroutine ->
            locationClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        coroutine.resume(result, onCancellation = null)
                    } else {
                        coroutine.resume(null, onCancellation = null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    coroutine.resume(it, onCancellation = null)
                }
                addOnFailureListener {
                    coroutine.resume(null, onCancellation = null)
                }
                addOnCanceledListener {
                    coroutine.cancel()
                }
            }
        }
    }
}