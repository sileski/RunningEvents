package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateRaceDateUseCase {

    fun execute(raceDate: String): ValidationResult {
        if (raceDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter race date"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}