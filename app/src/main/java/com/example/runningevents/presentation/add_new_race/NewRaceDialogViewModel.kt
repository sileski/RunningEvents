package com.example.runningevents.presentation.add_new_race

import android.net.Uri
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.models.RaceCategory
import com.example.runningevents.domain.use_cases.*
import com.example.runningevents.domain.use_cases.validation.*
import com.example.runningevents.utils.Event
import com.example.runningevents.utils.Result
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NewRaceDialogViewModel @Inject constructor(
    private val validateCityUseCase: ValidateCityUseCase,
    private val validateRaceNameUseCase: ValidateRaceNameUseCase,
    private val validateRaceWebsiteUseCase: ValidateWebsiteUseCase,
    private val validateRaceTimeUseCase: ValidateRaceTimeUseCase,
    private val validateRaceDateUseCase: ValidateRaceDateUseCase,
    private val validateCountryUseCase: ValidateCountryUseCase,
    private val validateImageUriUseCase: ValidateImageUriUseCase,
    private val validateRaceDescriptionUseCase: ValidateRaceDescriptionUseCase,
    private val addNewRaceUseCase: AddNewRaceUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCityPositionDetailsUseCase: GetCityPositionDetailsUseCase,
    private val getCitiesInCountryUseCase: GetCitiesInCountryUseCase
) : ViewModel() {

    private val _savingProgress = MutableLiveData(false)
    val savingProgress: LiveData<Boolean> = _savingProgress
    private val _saveRaceSuccess = MutableLiveData<Event<Unit>>()
    val saveRaceSuccess: LiveData<Event<Unit>> = _saveRaceSuccess
    private val _citiesInCountry = MutableLiveData<List<String>?>()
    val citiesInCountry: LiveData<List<String>?> = _citiesInCountry
    private val _eventShowError = MutableLiveData<Event<String>>()
    val eventShowError: LiveData<Event<String>> = _eventShowError
    private val _raceNameError = MutableLiveData<String?>()
    val raceNameError: LiveData<String?> = _raceNameError
    private val _raceDateError = MutableLiveData<String?>()
    val raceDateError: LiveData<String?> = _raceDateError
    private val _raceTimeError = MutableLiveData<String?>()
    val raceTimeError: LiveData<String?> = _raceTimeError
    private val _raceCountryError = MutableLiveData<String?>()
    val raceCountryError: LiveData<String?> = _raceCountryError
    private val _raceCityError = MutableLiveData<String?>()
    val raceCityError: LiveData<String?> = _raceCityError
    private val _raceWebsiteError = MutableLiveData<String?>()
    val raceWebsiteError: LiveData<String?> = _raceWebsiteError
    private val _raceDescriptionError = MutableLiveData<String?>()
    val raceDescriptionError: LiveData<String?> = _raceDescriptionError
    private val _raceImageError = MutableLiveData<String?>()
    val raceImageError: LiveData<String?> = _raceImageError

    private val currentUser = getUserUseCase.execute()
    var imageUri: Uri? = null

    fun validation(
        raceName: String,
        city: String,
        country: String,
        raceDate: String,
        raceTime: String,
        description: String,
        website: String
    ): Boolean {
        val raceNameValidationResult = validateRaceNameUseCase.execute(raceName = raceName)
        val cityValidationResult = validateCityUseCase.execute(city = city)
        val countryValidationResult = validateCountryUseCase.execute(country = country)
        val dateValidationResult = validateRaceDateUseCase.execute(raceDate = raceDate)
        val timeValidationResult = validateRaceTimeUseCase.execute(raceTime = raceTime)
        val websiteValidationResult = validateRaceWebsiteUseCase.execute(website = website)
        val descriptionValidationResult =
            validateRaceDescriptionUseCase.execute(description = description)
        val imageValidationResult = validateImageUriUseCase.execute(imageUri = imageUri)
        val error = listOf(
            raceNameValidationResult,
            cityValidationResult,
            countryValidationResult,
            dateValidationResult,
            timeValidationResult,
            descriptionValidationResult,
            websiteValidationResult,
            imageValidationResult
        ).any { !it.successful }
        _raceNameError.postValue(raceNameValidationResult.errorMessage)
        _raceCityError.postValue(cityValidationResult.errorMessage)
        _raceCountryError.postValue(countryValidationResult.errorMessage)
        _raceDateError.postValue(dateValidationResult.errorMessage)
        _raceTimeError.postValue(timeValidationResult.errorMessage)
        _raceWebsiteError.postValue(websiteValidationResult.errorMessage)
        _raceDescriptionError.postValue(descriptionValidationResult.errorMessage)
        _raceImageError.postValue(imageValidationResult.errorMessage)
        return !error
    }

    fun saveRace(
        city: String,
        country: String,
        raceName: String,
        websiteUrl: String,
        categories: List<RaceCategory>,
        description: String,
        dateTimestamp: Timestamp
    ) {
        var imageDownloadUrl = ""
        viewModelScope.launch {
            _savingProgress.postValue(true)
            val uploadImage = viewModelScope.async {
                val fileName = UUID.randomUUID().toString()
                when (val resultImage = saveImageUseCase.execute(fileName, imageUri!!)) {
                    is Result.Failure -> {
                        resultImage.message?.let {
                            _eventShowError.postValue(Event(it))
                        }
                        _savingProgress.postValue(false)
                    }
                    is Result.Success -> {
                        imageDownloadUrl = resultImage.data.toString()
                    }
                }
            }
            uploadImage.await()
            when (val positionResult =
                getCityPositionDetailsUseCase.execute(city = "$city,$country")) {
                is Result.Failure -> {
                    positionResult.message?.let {
                        _eventShowError.postValue(Event(it))
                    }
                    _savingProgress.postValue(false)
                }
                is Result.Success -> {
                    val latitude = positionResult.data?.data?.first()?.latitude ?: 0.0
                    val longitude = positionResult.data?.data?.first()?.longitude ?: 0.0
                    val geohash =
                        GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
                    val distanceFilter = getRaceFilterDistances(categories = categories)
                    val race = Race(
                        canceled = false,
                        city = city,
                        country = country,
                        createdBy = currentUser?.uid ?: "",
                        date = dateTimestamp,
                        geohash = geohash,
                        latitude = latitude,
                        longitude = longitude,
                        imageUrl = imageDownloadUrl,
                        raceId = "",
                        raceName = raceName,
                        websiteUrl = websiteUrl,
                        raceCategories = categories,
                        distanceFilter = distanceFilter,
                        description = description
                    )
                    viewModelScope.launch {
                        when (val resultSaving = addNewRaceUseCase.execute(race)) {
                            is Result.Failure -> {
                                resultSaving.message?.let {
                                    _eventShowError.postValue(Event(it))
                                }
                                _savingProgress.postValue(false)
                            }
                            is Result.Success -> {
                                _saveRaceSuccess.postValue(Event(Unit))
                            }
                        }
                    }
                }
            }
        }
    }

    fun convertDateToTimestamp(
        dateSeconds: Long,
        hour: Long,
        minutes: Long
    ): Timestamp {
        val offset: Int = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
        val hourMiliSeconds = TimeUnit.HOURS.toMillis(hour)
        val minutesMiliSeconds = TimeUnit.MINUTES.toMillis(minutes)
        val timeDate = dateSeconds + hourMiliSeconds + minutesMiliSeconds - offset
        val date = Date(timeDate)
        return Timestamp(date)
    }

    fun getCitiesInCountry(country: String) {
        viewModelScope.launch {
            when (val citiesResult = getCitiesInCountryUseCase.execute(country = country)) {
                is Result.Failure -> {
                    citiesResult.message?.let {
                        _eventShowError.postValue(Event(it))
                    }
                    _citiesInCountry.postValue(null)
                }
                is Result.Success -> {
                    citiesResult.data?.let {
                        _citiesInCountry.postValue(it.data)
                    }
                }
            }
        }
    }

    private fun getRaceFilterDistances(categories: List<RaceCategory>): List<String> {
        val filterDistances = mutableSetOf<String>()
        categories.map { it.distance }.forEach { distance ->
            val distanceWithoutUnit = distance.substring(0, distance.length - 2)
            when (distanceWithoutUnit.isDigitsOnly()) {
                false -> {
                    when (distance) {
                        "Marathon" -> filterDistances.add("4")
                        "Half-Marathon" -> filterDistances.add("3")
                    }
                }
                true -> {
                    val distanceNumber = distanceWithoutUnit.toInt()
                    when {
                        distanceNumber <= 5 -> filterDistances.add("1")
                        distanceNumber <= 10 -> filterDistances.add("2")
                    }
                }
            }
        }
        return filterDistances.toList()
    }

    fun getCategories(): List<String> {
        return listOf(
            "5km",
            "10km",
            "Half-Marathon",
            "Marathon"
        )
    }

    fun getCountries(): List<String> {
        return listOf(
            "Macedonia",
            "Serbia",
            "Croatia",
            "Montenegro",
            "Slovenia",
            "Greece",
            "Albania",
            "Bulgaria",
            "Hungary",
            "Italy",
            "Germany",
            "France",
            "Bosnia and Herzegovina",
            "Austria"
        ).sorted()
    }

    fun getCurrencies(): List<String> {
        return listOf(
            "EUR",
            "USD",
            "MKD",
            "RSD",
        ).sorted()
    }
}