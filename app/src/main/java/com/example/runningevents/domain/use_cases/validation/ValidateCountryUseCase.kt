package com.example.runningevents.domain.use_cases.validation

import com.example.runningevents.utils.ValidationResult

class ValidateCountryUseCase {

    fun execute(country: String): ValidationResult {
        if (country.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please select a country"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}