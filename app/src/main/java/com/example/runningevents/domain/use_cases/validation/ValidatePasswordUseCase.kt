package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidatePasswordUseCase {

    fun execute(password: String): ValidationResult {
        if (password.length < 6) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password must contain of at least 6 characters"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}