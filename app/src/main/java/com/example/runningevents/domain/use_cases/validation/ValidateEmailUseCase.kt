package com.example.runningevents.domain.use_cases.validation

import android.util.Patterns
import com.example.runningevents.utils.ValidationResult

class ValidateEmailUseCase {

    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "E-mail can't be blank"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "E-mail is not valid"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}