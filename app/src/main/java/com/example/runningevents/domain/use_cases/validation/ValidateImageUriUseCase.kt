package com.example.runningevents.domain.use_cases.validation

import android.net.Uri
import com.example.runningevents.utils.ValidationResult

class ValidateImageUriUseCase {

    fun execute(imageUri: Uri?): ValidationResult {
        if (imageUri == null) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please select an image"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}