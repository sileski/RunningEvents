package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateConfirmPasswordUseCase {

    fun execute(password: String, confirmPassword: String): ValidationResult {
        if (confirmPassword != password) {
            return ValidationResult(
                successful = false,
                errorMessage = "Passwords don't match"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}