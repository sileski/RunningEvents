package com.example.runningevents.presentation.race_filters

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import com.example.runningevents.R
import com.example.runningevents.utils.Constants.KEY_ON_BACK_FILTER_RACES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RaceFiltersFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    private lateinit var sortRaces: ListPreference
    private lateinit var radius: SeekBarPreference
    private lateinit var checkBoxDistanceAll: CheckBoxPreference
    private lateinit var checkBoxDistance5km: CheckBoxPreference
    private lateinit var checkBoxDistance10km: CheckBoxPreference
    private lateinit var checkBoxDistanceHalfMarathon: CheckBoxPreference
    private lateinit var checkBoxDistanceMarathon: CheckBoxPreference
    private var onBackRefresh = false
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_filter_races, rootKey)

        sortRaces = findPreference(getString(R.string.races_sort_preference_key))!!
        radius = findPreference(getString(R.string.radius_preference_key))!!
        checkBoxDistanceAll = findPreference(getString(R.string.filter_distance_option_all_key))!!
        checkBoxDistance5km = findPreference(getString(R.string.filter_distance_option_5km_key))!!
        checkBoxDistance10km = findPreference(getString(R.string.filter_distance_option_10km_key))!!
        checkBoxDistanceHalfMarathon =
            findPreference(getString(R.string.filter_distance_option_half_key))!!
        checkBoxDistanceMarathon =
            findPreference(getString(R.string.filter_distance_option_marathon_key))!!


        hideShowRadioSeekBar(racesSorted = sortRaces.value)
        sortRaces.setOnPreferenceChangeListener { _, newValue ->
            onBackRefresh = true
            hideShowRadioSeekBar(racesSorted = newValue.toString())
            true
        }
        radius.setOnPreferenceChangeListener { _, _ ->
            onBackRefresh = true
            true
        }

        checkBoxDistanceAll.onPreferenceClickListener = this
        checkBoxDistance5km.onPreferenceClickListener = this
        checkBoxDistance10km.onPreferenceClickListener = this
        checkBoxDistanceHalfMarathon.onPreferenceClickListener = this
        checkBoxDistanceMarathon.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        onBackRefresh = true
        when (preference?.key) {
            getString(R.string.filter_distance_option_all_key) -> {
                if (checkBoxDistanceAll.isChecked) {
                    checkBoxDistance5km.isChecked = false
                    checkBoxDistance10km.isChecked = false
                    checkBoxDistanceHalfMarathon.isChecked = false
                    checkBoxDistanceMarathon.isChecked = false
                } else if (!checkBoxDistance5km.isChecked && !checkBoxDistance10km.isChecked && !checkBoxDistanceHalfMarathon.isChecked && !checkBoxDistanceMarathon.isChecked) {
                    checkBoxDistanceAll.isChecked = true
                }
                return true
            }
            getString(R.string.filter_distance_option_5km_key), getString(R.string.filter_distance_option_10km_key), getString(
                R.string.filter_distance_option_half_key
            ), getString(R.string.filter_distance_option_marathon_key) -> {
                if (checkBoxDistanceAll.isChecked) {
                    checkBoxDistanceAll.isChecked = false
                } else if (!checkBoxDistance5km.isChecked && !checkBoxDistance10km.isChecked && !checkBoxDistanceHalfMarathon.isChecked && !checkBoxDistanceMarathon.isChecked) {
                    checkBoxDistanceAll.isChecked = true
                } else if (checkBoxDistance5km.isChecked && checkBoxDistance10km.isChecked && checkBoxDistanceHalfMarathon.isChecked && checkBoxDistanceMarathon.isChecked) {
                    checkBoxDistance5km.isChecked = false
                    checkBoxDistance10km.isChecked = false
                    checkBoxDistanceHalfMarathon.isChecked = false
                    checkBoxDistanceMarathon.isChecked = false
                    checkBoxDistanceAll.isChecked = true
                }
                return true
            }
            else -> return false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.app_toolbar)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        toolbar.findViewById<TextView>(R.id.toolbar_title).text = getString(R.string.filter_races)
        toolbar.setNavigationOnClickListener {
            onBackClick()
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackClick()
                }
            })
    }

    private fun onBackClick() {
        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(KEY_ON_BACK_FILTER_RACES, onBackRefresh)
            popBackStack()
        }
    }

    private fun hideShowRadioSeekBar(racesSorted: String) {
        radius.isVisible = racesSorted == getString(R.string.races_sort_option_nearby_key)
    }
}