package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateRaceDescriptionUseCase {

    fun execute(description: String): ValidationResult {
        if (description.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter race description"
            )
        }
        if (description.length < 100) {
            return ValidationResult(
                successful = false,
                errorMessage = "Race description is too short, please enter at least 100 characters"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}