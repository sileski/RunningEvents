package com.example.runningevents.data.dto

import com.google.firebase.Timestamp

data class RaceDto(
    val canceled: Boolean = false,
    val city: String = "",
    val country: String = "",
    val description: String = "",
    val createdBy: String = "",
    val date: Timestamp = Timestamp.now(),
    val geohash: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val raceId: String = "",
    val raceName: String = "",
    val websiteUrl: String = "",
    val categories: List<RaceCategoryDto> = emptyList(),
    val distanceFilter: List<String> = emptyList()
)

data class RaceCategoryDto(
    val currency: String = "",
    val date: Timestamp = Timestamp.now(),
    val distance: String = "",
    val price: Double = 0.0
)
