package com.example.runningevents.domain.use_cases

import android.net.Uri
import com.example.runningevents.domain.repositories.ImagesStorage
import com.example.runningevents.utils.Result
import javax.inject.Inject

class SaveImageUseCase @Inject constructor(
    private val imagesStorage: ImagesStorage
) {
    suspend fun execute(fileName: String, imageUri: Uri): Result<Uri> {
        return imagesStorage.saveImage(fileName = fileName, imageUri = imageUri)
    }
}