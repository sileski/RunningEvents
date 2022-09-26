package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateRaceTimeUseCase {

    fun execute(raceTime: String): ValidationResult {
        if (raceTime.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter race starting time"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}