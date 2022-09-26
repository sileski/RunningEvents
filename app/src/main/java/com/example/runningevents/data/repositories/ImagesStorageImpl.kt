package com.example.runningevents.data.repositories

import android.net.Uri
import com.example.runningevents.domain.repositories.ImagesStorage
import com.example.runningevents.utils.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ImagesStorageImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : ImagesStorage {

    private val storageReference = firebaseStorage.reference

    override suspend fun saveImage(fileName: String, imageUri: Uri): Result<Uri> {
        return try {
            val imagesReference = storageReference.child("images/$fileName")
            val imageDownloadUri =
                imagesReference.putFile(imageUri).await().storage.downloadUrl.await()
            Result.Success(imageDownloadUri)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }
}