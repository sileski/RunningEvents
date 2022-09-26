package com.example.runningevents.domain.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Race(
    val canceled: Boolean,
    val city: String,
    val country: String,
    val description: String,
    val createdBy: String,
    val date: Timestamp,
    val geohash: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val raceId: String,
    val raceName: String,
    val websiteUrl: String,
    val raceCategories: List<RaceCategory>,
    val distanceFilter: List<String>
) : Parcelable
