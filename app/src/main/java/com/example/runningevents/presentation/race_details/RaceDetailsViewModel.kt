package com.example.runningevents.presentation.race_details

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RaceDetailsViewModel @Inject constructor(

) : ViewModel() {

    fun getDateFormat(date: Date): String {
        val dateFormat = SimpleDateFormat("E, d MMM yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getTimeFormat(date: Date): String {
        val timeFormat = SimpleDateFormat("HH:mm z", Locale.getDefault())
        return timeFormat.format(date)
    }

    fun getCurrencyFormat(price: Double, currency: String): String {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance(currency)
        return currencyFormat.format(price)
    }
}