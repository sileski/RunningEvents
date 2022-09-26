package com.example.runningevents.presentation.races

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningevents.domain.location.LocationTracker
import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.use_cases.*
import com.example.runningevents.utils.Event
import com.example.runningevents.utils.RacesSortedBy
import com.example.runningevents.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(
    private val getRacesByDateUseCase: GetRacesByDateUseCase,
    private val getRacesByLocationUseCase: GetRacesByLocationUseCase,
    private val getRaceFilterDistancesUseCase: GetRaceFilterDistancesUseCase,
    private val getRacesSortedByUseCase: GetRacesSortedByUseCase,
    private val getRacesRadiusDistance: GetRacesRadiusDistance,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _races = MutableLiveData<List<Race>?>()
    val races: LiveData<List<Race>?> = _races
    private val _eventShowError = MutableLiveData<Event<String>>()
    val eventShowError: LiveData<Event<String>> = _eventShowError
    private val _requestPermission = MutableLiveData<Event<Unit>>()
    val requestPermission: LiveData<Event<Unit>> = _requestPermission
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getRaces(true)
    }

    fun getRaces(
        isRefreshing: Boolean,
    ) {
        val racesDistances = getRaceFilterDistancesUseCase.execute()
        val racesSortedBy = getRacesSortedByUseCase.execute()
        if (isRefreshing) {
            _races.postValue(null)
        }
        _isLoading.postValue(true)
        when (racesSortedBy) {
            RacesSortedBy.DATE -> {
                getRacesByDate(isRefreshing = isRefreshing, racesDistances = racesDistances)
            }
            RacesSortedBy.LOCATION -> {
                getRacesByLocation(isRefreshing = isRefreshing, racesDistances = racesDistances)
            }
        }
    }

    private fun getRacesByDate(isRefreshing: Boolean, racesDistances: List<String>) {
        viewModelScope.launch {
            when (val newRaces = getRacesByDateUseCase.execute(
                isRefreshing = isRefreshing,
                racesDistanceFilter = racesDistances
            )) {
                is Result.Success -> {
                    val allRaces: MutableList<Race> =
                        (_races.value ?: emptyList()).toMutableList()
                    newRaces.data?.let {
                        allRaces.addAll(it)
                    }
                    _races.postValue(allRaces)
                    _isLoading.postValue(false)
                }
                is Result.Failure -> {
                    newRaces.message?.let {
                        _eventShowError.postValue(Event(it))
                    }
                    _isLoading.postValue(false)
                }
            }
        }
    }

    private fun getRacesByLocation(isRefreshing: Boolean, racesDistances: List<String>) {
        viewModelScope.launch {
            locationTracker.getUserCurrentLocation().let { location ->
                if (location != null) {
                    val radius = getRacesRadiusDistance.execute()
                    val newRaces = getRacesByLocationUseCase.execute(
                        isRefreshing = isRefreshing,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        radius = radius,
                        racesDistanceFilter = racesDistances
                    )
                    when (newRaces) {
                        is Result.Success -> {
                            val allRaces: MutableList<Race> =
                                (_races.value ?: emptyList()).toMutableList()
                            newRaces.data?.let {
                                allRaces.addAll(it)
                            }
                            _races.postValue(allRaces)
                            _isLoading.postValue(false)
                        }
                        is Result.Failure -> {
                            newRaces.message?.let {
                                _eventShowError.postValue(Event(it))
                            }
                            _isLoading.postValue(false)
                        }
                    }
                } else {
                    _isLoading.postValue(false)
                    _eventShowError.postValue(Event("Unable to get location. Please grant location permission."))
                    _requestPermission.postValue(Event(Unit))
                }
            }
        }
    }
}
