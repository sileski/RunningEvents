package com.example.runningevents.presentation.start_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.runningevents.domain.use_cases.GetUserUseCase
import com.example.runningevents.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _navigateToRaces = MutableLiveData<Event<Unit>>()
    val navigateToRaces: LiveData<Event<Unit>> = _navigateToRaces
    private val _navigateToLogin = MutableLiveData<Event<Unit>>()
    val navigateToLogin: LiveData<Event<Unit>> = _navigateToLogin

    init {
        val user = getUserUseCase.execute()
        if (user != null) {
            _navigateToRaces.postValue(Event(Unit))
        } else {
            _navigateToLogin.postValue(Event(Unit))
        }
    }
}