package com.example.runningevents.presentation.login_signup_container.social_login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningevents.domain.use_cases.LogInAnonymouslyUseCase
import com.example.runningevents.domain.use_cases.LogInWithGoogleUseCase
import com.example.runningevents.utils.Event
import com.example.runningevents.utils.Result
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialLoginViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LogInWithGoogleUseCase,
    private val logInAnonymouslyUseCase: LogInAnonymouslyUseCase
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _eventShowError = MutableLiveData<Event<String>>()
    val eventShowError: LiveData<Event<String>> = _eventShowError
    private val _loginSuccess = MutableLiveData<Event<Unit>>()
    val loginSuccess: LiveData<Event<Unit>> = _loginSuccess

    fun loginGoogle(firebaseCredential: AuthCredential) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val googleLoginResult = loginWithGoogleUseCase.execute(firebaseCredential)) {
                is Result.Failure -> {
                    googleLoginResult.message?.let {
                        _eventShowError.postValue(Event(it))
                    }
                    _isLoading.postValue(false)
                }
                is Result.Success -> {
                    _isLoading.postValue(false)
                    _loginSuccess.postValue(Event(Unit))
                }
            }
        }
    }

    fun loginAnonymously() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val anonymousLoginResult = logInAnonymouslyUseCase.execute()) {
                is Result.Failure -> {
                    anonymousLoginResult.message?.let {
                        _eventShowError.postValue(Event(it))
                    }
                    _isLoading.postValue(false)
                }
                is Result.Success -> {
                    _isLoading.postValue(false)
                    _loginSuccess.postValue(Event(Unit))
                }
            }
        }
    }
}