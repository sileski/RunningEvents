package com.example.runningevents.domain.use_cases.validation

import android.util.Patterns
import com.example.runningevents.utils.ValidationResult

class ValidateWebsiteUseCase {

    fun execute(website: String): ValidationResult {
        if (website.isNotEmpty() && !Patterns.WEB_URL.matcher(website).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter a correct website URL"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}