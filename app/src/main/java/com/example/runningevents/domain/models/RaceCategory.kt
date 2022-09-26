package com.example.runningevents.domain.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class RaceCategory(
    val currency: String,
    val date: Timestamp,
    val distance: String,
    val price: Double
) : Parcelable
