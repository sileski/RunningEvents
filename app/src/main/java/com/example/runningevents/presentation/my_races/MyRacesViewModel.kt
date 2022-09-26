package com.example.runningevents.presentation.my_races

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.use_cases.DeleteRaceUseCase
import com.example.runningevents.domain.use_cases.GetMyRacesUseCase
import com.example.runningevents.domain.use_cases.GetUserUseCase
import com.example.runningevents.utils.Event
import com.example.runningevents.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRacesViewModel @Inject constructor(
    private val getMyRacesUseCase: GetMyRacesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val deleteRaceUseCase: DeleteRaceUseCase

) : ViewModel() {

    private val _races = MutableLiveData<List<Race>?>()
    val races: LiveData<List<Race>?> = _races
    private val _eventShowError = MutableLiveData<Event<String>>()
    val eventShowError: LiveData<Event<String>> = _eventShowError
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val currentUser = getUserUseCase.execute()

    init {
        getMyRaces(isRefreshing = true)
    }

    fun getMyRaces(isRefreshing: Boolean) {
        currentUser?.uid?.let {
            if (isRefreshing) {
                _races.postValue(null)
            }
            _isLoading.postValue(true)
            viewModelScope.launch {
                when (val myRaces =
                    getMyRacesUseCase.execute(isRefreshing = isRefreshing, userId = it)) {
                    is Result.Failure -> {
                        myRaces.message?.let {
                            _eventShowError.postValue(Event(it))
                        }
                        _isLoading.postValue(false)
                    }
                    is Result.Success -> {
                        val allMyRaces: MutableList<Race> =
                            (_races.value ?: emptyList()).toMutableList()
                        myRaces.data?.let {
                            allMyRaces.addAll(it)
                        }
                        _races.postValue(allMyRaces)
                        _isLoading.postValue(false)
                    }
                }
            }
        }
    }

    fun deleteRace(race: Race) {
        viewModelScope.launch {
            when (val result = deleteRaceUseCase.execute(raceId = race.raceId)) {
                is Result.Failure -> {
                    result.message?.let {
                        _eventShowError.postValue(Event(it))
                    }
                }
                is Result.Success -> {
                    val allMyRaces: MutableList<Race> =
                        (_races.value ?: emptyList()).toMutableList()
                    allMyRaces.remove(race)
                    _races.postValue(allMyRaces)
                }
            }
        }
    }

    fun isUserLogged(): Boolean {
        currentUser?.let {
            return !it.isAnonymous
        }
        return false
    }
}