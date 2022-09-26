package com.example.runningevents.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.runningevents.R
import com.example.runningevents.presentation.common.ErrorSnackbar.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var loginPreference: Preference
    private lateinit var logoutPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey)

        loginPreference = findPreference("login")!!
        logoutPreference = findPreference("logout")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (viewModel.isUserLoggedIn()) {
            loginPreference.isVisible = false
        } else {
            logoutPreference.isVisible = false
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        loginPreference.setOnPreferenceClickListener {
            viewModel.logOut()
            false
        }

        logoutPreference.setOnPreferenceClickListener {
            viewModel.logOut()
            false
        }
    }

    private fun initObservers() {
        viewModel.navigateToLoginPage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                navigateToLogin()
            }
        })
        viewModel.eventShowError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { error ->
                showErrorSnackbar(view = view, errorMessage = error)
            }
        })
    }

    private fun navigateToLogin() {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToLoginSignupContainerFragment())
    }
}