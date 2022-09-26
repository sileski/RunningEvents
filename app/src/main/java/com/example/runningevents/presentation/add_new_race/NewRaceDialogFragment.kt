package com.example.runningevents.presentation.add_new_race

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.runningevents.R
import com.example.runningevents.databinding.CategoryItemBinding
import com.example.runningevents.databinding.FragmentDialogNewRaceBinding
import com.example.runningevents.domain.models.RaceCategory
import com.example.runningevents.presentation.common.ErrorSnackbar.showErrorSnackbar
import com.example.runningevents.utils.Constants.MAXIMUM_RACE_CATEGORIES
import com.example.runningevents.utils.Constants.ONE_DAY_IN_MILLIS
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewRaceDialogFragment : DialogFragment() {

    private val viewModel: NewRaceDialogViewModel by viewModels()
    private lateinit var binding: FragmentDialogNewRaceBinding
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapterCategories: ArrayAdapter<*>
    private lateinit var adapterCurrencies: ArrayAdapter<*>
    private lateinit var adapterCountries: ArrayAdapter<*>
    private lateinit var adapterCities: ArrayAdapter<*>
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var dataPicker: MaterialDatePicker<*>
    private var selectedCategory: HashMap<String, RaceCategory> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_NewRaceDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogNewRaceBinding.inflate(inflater, container, false)
        dialog?.window?.attributes?.windowAnimations = R.style.Theme_RunningEvents_Slide
        selectImageResultLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenu()
        initObservers()

        dataPicker = datePicker().build()
        timePicker = MaterialTimePicker()
        setDatePicker()
        setTimePicker()

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
        binding.imageSelect.setOnClickListener {
            openImages()
        }
        binding.newCategoryButton.setOnClickListener {
            if (binding.containerCategories.childCount <= MAXIMUM_RACE_CATEGORIES) {
                addNewCategoryField()
            }
        }
        binding.dateEditText.setOnClickListener {
            showDatePicker(dataPicker)
        }
        binding.timeEditText.setOnClickListener {
            showTimePicker(timePicker)
        }

        adapterCountries = ArrayAdapter(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            viewModel.getCountries()
        )
        binding.countryInputText.setAdapter(adapterCountries)

        adapterCategories = ArrayAdapter(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            viewModel.getCategories()
        )
        adapterCurrencies = ArrayAdapter(
            view.context,
            android.R.layout.simple_dropdown_item_1line,
            viewModel.getCurrencies()
        )
        addNewCategoryField()
        binding.countryInputText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedCountry = adapterCountries.getItem(position).toString()
                viewModel.getCitiesInCountry(country = selectedCountry)
            }
    }

    private fun setToolbarMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_add_new_race)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_save -> {
                        saveRace()
                    }
                }
                true
            }
        }
    }

    private fun initObservers() {
        viewModel.citiesInCountry.observe(viewLifecycleOwner, Observer { cities ->
            if (cities != null) {
                adapterCities = ArrayAdapter(
                    view?.context!!,
                    android.R.layout.simple_dropdown_item_1line,
                    cities
                )
                binding.cityInputText.setAdapter(adapterCities)
                if (adapterCities.count > 0) {
                    binding.cityInputText.setText(adapterCities.getItem(0).toString(), false)
                }
            } else {
                binding.cityInputText.setText("")
            }
        })
        viewModel.savingProgress.observe(viewLifecycleOwner) {
            if (it) {
                binding.savingProgressLayout.visibility = View.VISIBLE
            } else {
                binding.savingProgressLayout.visibility = View.GONE
            }
        }
        viewModel.saveRaceSuccess.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.savingProgress.isIndeterminate = false
                binding.savingProgress.progress = 100
                binding.savingResult.text = getString(R.string.race_saved_successfully)
                lifecycleScope.launch {
                    delay(1000L)
                    dismiss()
                }
            }
        })
        viewModel.eventShowError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { error ->
                showErrorSnackbar(view = view, errorMessage = error)
            }
        })
        viewModel.raceNameError.observe(viewLifecycleOwner, Observer {
            binding.raceNameInputLayout.error = it
        })
        viewModel.raceCountryError.observe(viewLifecycleOwner, Observer {
            binding.countryInputLayout.error = it
        })
        viewModel.raceCityError.observe(viewLifecycleOwner, Observer {
            binding.cityInputLayout.error = it
        })
        viewModel.raceTimeError.observe(viewLifecycleOwner, Observer {
            binding.timeInputLayout.error = it
        })
        viewModel.raceDateError.observe(viewLifecycleOwner, Observer {
            binding.dateInputLayout.error = it
        })
        viewModel.raceDescriptionError.observe(viewLifecycleOwner, Observer {
            binding.descriptionInputLayout.error = it
        })
        viewModel.raceWebsiteError.observe(viewLifecycleOwner, Observer {
            binding.websiteInputLayout.error = it
        })
        viewModel.raceImageError.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.imageError.visibility = View.VISIBLE
                binding.imageError.text = it
            } else {
                binding.imageError.visibility = View.GONE
            }
        })
    }

    private fun openImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        selectImageLauncher.launch(Intent.createChooser(intent, getString(R.string.select_image)))
    }

    private fun selectImageResultLauncher() {
        selectImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.data
                    binding.imageSelect.load(imageUri) {
                        listener(
                            onSuccess = { _, _ ->
                                binding.cameraIcon.visibility = View.GONE
                            },
                            onError = { _, _ ->
                                showErrorSnackbar(
                                    view = view,
                                    errorMessage = getString(R.string.cant_load_image)
                                )
                            })
                    }
                    viewModel.imageUri = imageUri
                    binding.imageError.visibility = View.GONE
                } else {

                }
            }
    }

    private fun addNewCategoryField() {
        val containerCategories = this.binding.containerCategories
        val binding = CategoryItemBinding.inflate(layoutInflater, null, false)
        val view = binding.root
        val margins = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        margins.topMargin = 10
        view.layoutParams = margins
        binding.distanceInputText.setAdapter(adapterCategories)
        binding.distanceInputText.setText(adapterCategories.getItem(0).toString(), false)
        binding.currencyInputText.setAdapter(adapterCurrencies)
        binding.currencyInputText.setText(adapterCurrencies.getItem(0).toString(), false)

        binding.timeEditText.setText("08:00")
        val time = binding.timeEditText.text.toString().split(":")
        val hour = time[0].toLong()
        val minutes = time[1].toLong()

        val uniqueId = UUID.randomUUID().toString()
        selectedCategory[uniqueId] =
            RaceCategory(
                distance = binding.distanceInputText.text.toString(),
                price = 0.0,
                currency = binding.currencyInputText.text.toString(),
                date = viewModel.convertDateToTimestamp(
                    dateSeconds = dataPicker.selection as Long,
                    hour = hour,
                    minutes = minutes
                )
            )

        (binding.distanceInputLayout.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                run {
                    val selected = selectedCategory[uniqueId]
                    selected?.let {
                        selectedCategory[uniqueId] = selected.copy(
                            distance = adapterCategories.getItem(position).toString()
                        )
                    }
                }
            }
        (binding.currencyInputLayout.editText as AutoCompleteTextView).onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                run {
                    val selected = selectedCategory[uniqueId]
                    selected?.let {
                        selectedCategory[uniqueId] = selected.copy(
                            currency = adapterCurrencies.getItem(position).toString()
                        )
                    }
                }
            }
        binding.priceInputText.addTextChangedListener {
            val price = if (it?.isNotEmpty() == true) {
                it.toString().toDouble()
            } else {
                0.0
            }
            val selected = selectedCategory[uniqueId]
            selected?.let {
                selectedCategory[uniqueId] = selected.copy(
                    price = price
                )
            }
        }
        binding.timeEditText.setOnClickListener {
            showCategoryTimePicker(textInputEditText = binding.timeEditText) { hour, minutes ->
                val selected = selectedCategory[uniqueId]
                selected?.let {
                    selectedCategory[uniqueId] = selected.copy(
                        date = viewModel.convertDateToTimestamp(
                            dateSeconds = dataPicker.selection as Long,
                            hour = hour,
                            minutes = minutes
                        )
                    )
                }
            }
        }
        binding.deleteBtn.setOnClickListener {
            if (containerCategories.childCount > 1) {
                selectedCategory.remove(uniqueId)
                containerCategories.removeView(view)
            }
        }
        containerCategories.addView(view)
    }

    private fun setDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(
            DateValidatorPointForward.from(
                System.currentTimeMillis() + ONE_DAY_IN_MILLIS
            )
        )
        dataPicker = datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        dataPicker.addOnPositiveButtonClickListener {
            binding.dateEditText.setText(dataPicker.headerText)
        }
    }

    private fun showDatePicker(picker: MaterialDatePicker<*>) {
        picker.show(parentFragmentManager, "datepicker")
    }

    private fun setTimePicker() {
        val is24HourFormat = is24HourFormat(context)
        var format = if (is24HourFormat) {
            TimeFormat.CLOCK_24H
        } else {
            TimeFormat.CLOCK_12H
        }
        timePicker = MaterialTimePicker.Builder()
            .setTitleText(getString(R.string.select_time))
            .setTimeFormat(format)
            .setHour(12)
            .setMinute(0)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val time: String = timePicker.hour.toString() + ":" + timePicker.minute
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            try {
                val date: Date = timeFormat.parse(time) as Date
                val localTime: String = timeFormat.format(date)
                binding.timeEditText.setText(localTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    private fun showTimePicker(
        timePicker: MaterialTimePicker
    ) {
        timePicker.show(parentFragmentManager, "timepicker")
    }

    private fun showCategoryTimePicker(
        textInputEditText: TextInputEditText,
        hour: (Long, Long) -> Unit
    ) {
        val is24HourFormat = is24HourFormat(context)
        var format = if (is24HourFormat) {
            TimeFormat.CLOCK_24H
        } else {
            TimeFormat.CLOCK_12H
        }
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(getString(R.string.select_time))
            .setTimeFormat(format)
            .setHour(12)
            .setMinute(0)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val time: String = timePicker.hour.toString() + ":" + timePicker.minute
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            try {
                val date: Date = timeFormat.parse(time) as Date
                val localTime: String = timeFormat.format(date)
                textInputEditText.setText(localTime)
                hour(timePicker.hour.toLong(), timePicker.minute.toLong())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        timePicker.show(parentFragmentManager, "category_timepicker")
    }

    private fun saveRace() {
        val raceName = binding.raceNameEditText.text.toString()
        val city = binding.cityInputText.text.toString()
        val country = binding.countryInputText.text.toString()
        val raceDate = binding.dateEditText.text.toString()
        val raceTime = binding.timeEditText.text.toString()
        val website = binding.websiteEditText.text.toString()
        val raceDescription = binding.descriptionEditText.text.toString()
        if (viewModel.validation(
                raceName = raceName,
                city = city,
                country = country,
                raceDate = raceDate,
                raceTime = raceTime,
                website = website,
                description = raceDescription
            )
        ) {
            val dateSeconds = dataPicker.selection as Long
            val hour = timePicker.hour.toLong()
            val minutes = timePicker.minute.toLong()
            viewModel.saveRace(
                city = city,
                country = country,
                raceName = raceName,
                websiteUrl = website,
                description = raceDescription,
                dateTimestamp = viewModel.convertDateToTimestamp(
                    dateSeconds = dateSeconds,
                    hour = hour,
                    minutes = minutes
                ),
                categories = selectedCategory.values.toList()
            )
        }
    }
}