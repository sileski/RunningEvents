package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateFullNameUseCase {

    fun execute(fullName: String): ValidationResult {
        if (fullName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter name"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}