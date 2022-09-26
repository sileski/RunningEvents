package com.example.runningevents.presentation.login_signup_container.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningevents.domain.use_cases.LogInWithEmailAndPasswordUseCase
import com.example.runningevents.domain.use_cases.validation.ValidateEmailUseCase
import com.example.runningevents.domain.use_cases.validation.ValidatePasswordUseCase
import com.example.runningevents.utils.Event
import com.example.runningevents.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val loginInWithEmailAndPasswordUseCase: LogInWithEmailAndPasswordUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
) : ViewModel() {

    private val _eventShowError = MutableLiveData<Event<String>>()
    val eventShowError: LiveData<Event<String>> = _eventShowError
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private fun validation(email: String, password: String): Boolean {
        val emailValidationResult = validateEmailUseCase.execute(email = email)
        val passwordValidationResult = validatePasswordUseCase.execute(password = password)
        val error = listOf(
            emailValidationResult,
            passwordValidationResult
        ).any { !it.successful }
        _emailError.postValue(emailValidationResult.errorMessage)
        _passwordError.postValue(passwordValidationResult.errorMessage)
        return !error
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        if (validation(email = email, password = password)) {
            _isLoading.postValue(true)
            viewModelScope.launch {
                when (val loginStatus = loginInWithEmailAndPasswordUseCase.execute(
                    email = email,
                    password = password
                )) {
                    is Result.Failure -> {
                        loginStatus.message?.let {
                            _eventShowError.postValue(Event(it))
                        }
                        _isLoading.postValue(false)
                    }
                    is Result.Success -> {
                        _isLoading.postValue(false)
                    }
                }
            }
        }
    }
}