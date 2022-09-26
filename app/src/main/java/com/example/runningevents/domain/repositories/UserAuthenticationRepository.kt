package com.example.runningevents.domain.repositories

import com.example.runningevents.utils.Result
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface UserAuthenticationRepository {

    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogleAccount(firebaseCredential: AuthCredential): Result<Unit>
    suspend fun loginAnonymously(): Result<Unit>
    fun logOut(): Result<Unit>
    fun getUser(): FirebaseUser?
}