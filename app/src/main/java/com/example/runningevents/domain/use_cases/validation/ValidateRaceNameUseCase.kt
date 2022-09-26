package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateRaceNameUseCase {

    fun execute(raceName: String): ValidationResult {
        if (raceName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter a race name"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}