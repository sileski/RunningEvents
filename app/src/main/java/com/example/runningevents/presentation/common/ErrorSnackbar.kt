package com.example.runningevents.presentation.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

object ErrorSnackbar {
    fun showErrorSnackbar(view: View?, errorMessage: String) {
        view?.let {
            Snackbar.make(it, errorMessage, Snackbar.LENGTH_LONG).show()
        }
    }
}