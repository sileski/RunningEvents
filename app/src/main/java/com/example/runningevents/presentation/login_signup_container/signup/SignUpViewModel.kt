package com.example.runningevents.presentation.login_signup_container.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningevents.domain.use_cases.SignUpWithEmailAndPasswordUseCase
import com.example.runningevents.domain.use_cases.validation.ValidateConfirmPasswordUseCase
import com.example.runningevents.domain.use_cases.validation.ValidateEmailUseCase
import com.example.runningevents.domain.use_cases.validation.ValidateFullNameUseCase
import com.example.runningevents.domain.use_cases.validation.ValidatePasswordUseCase
import com.example.runningevents.utils.Event
import com.example.runningevents.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithEmailAndPasswordUseCase: SignUpWithEmailAndPasswordUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase,
    private val validateFullNameUseCase: ValidateFullNameUseCase
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _eventShowError = MutableLiveData<Event<String>>()
    val eventShowError: LiveData<Event<String>> = _eventShowError
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError
    private val _fullNameError = MutableLiveData<String?>()
    val fullNameError: LiveData<String?> = _fullNameError

    private fun validation(
        email: String,
        password: String,
        fullName: String,
        confirmPassword: String
    ): Boolean {
        val emailValidationResult = validateEmailUseCase.execute(email = email)
        val passwordValidationResult = validatePasswordUseCase.execute(password = password)
        val confirmPasswordValidationResult = validateConfirmPasswordUseCase.execute(
            password = password,
            confirmPassword = confirmPassword
        )
        val fullNameValidationResult = validateFullNameUseCase.execute(fullName = fullName)
        val error = listOf(
            emailValidationResult,
            passwordValidationResult,
            confirmPasswordValidationResult,
            fullNameValidationResult
        ).any { !it.successful }
        _emailError.postValue(emailValidationResult.errorMessage)
        _passwordError.postValue(passwordValidationResult.errorMessage)
        _confirmPasswordError.postValue(confirmPasswordValidationResult.errorMessage)
        _fullNameError.postValue(fullNameValidationResult.errorMessage)
        return !error
    }

    fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        fullName: String,
        confirmPassword: String
    ) {
        if (validation(
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                fullName = fullName
            )
        ) {
            _isLoading.postValue(true)
            viewModelScope.launch {
                when (val signUpStatus =
                    signUpWithEmailAndPasswordUseCase.execute(email = email, password = password)) {
                    is Result.Failure -> {
                        _eventShowError.postValue(Event(signUpStatus.message.toString()))
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