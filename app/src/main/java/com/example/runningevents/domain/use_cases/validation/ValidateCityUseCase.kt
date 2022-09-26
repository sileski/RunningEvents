package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateCityUseCase {

    fun execute(city: String): ValidationResult {
        if (city.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please select a city"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}