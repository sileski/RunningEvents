package com.example.runningevents.domain.repositories

import android.net.Uri
import com.example.runningevents.utils.Result

interface ImagesStorage {

    suspend fun saveImage(fileName: String, imageUri: Uri): Result<Uri>
}