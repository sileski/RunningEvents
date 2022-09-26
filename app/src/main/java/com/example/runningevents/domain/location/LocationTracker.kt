package com.example.runningevents.domain.location

import android.location.Location

interface LocationTracker {

    suspend fun getUserCurrentLocation(): Location?
}