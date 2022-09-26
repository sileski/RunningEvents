package com.example.runningevents.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.runningevents.domain.use_cases.GetUserUseCase
import com.example.runningevents.domain.use_cases.LogOutUseCase
import com.example.runningevents.utils.Event
import com.example.runningevents.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val logOutUseCase: LogOutUseCase
) : ViewModel() {

    private val _navigateToLoginPage = MutableLiveData<Event<Unit>>()
    val navigateToLoginPage: LiveData<Event<Unit>> = _navigateToLoginPage
    private val _eventShowError = MutableLiveData<Event<String>>()
    val eventShowError: LiveData<Event<String>> = _eventShowError

    fun isUserLoggedIn(): Boolean {
        getUserUseCase.execute()?.let {
            return !it.isAnonymous
        }
        return false
    }

    fun logOut() {
        when (val logout = logOutUseCase.execute()) {
            is Result.Failure -> {
                logout.message?.let {
                    _eventShowError.postValue(Event(it))
                }
            }
            is Result.Success -> {
                _navigateToLoginPage.postValue(Event(Unit))
            }
        }
    }
}