package com.example.runningevents.data.repositories

import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.example.runningevents.utils.Result
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserAuthenticationRepositoryImpl @Inject constructor(
    private val authentication: FirebaseAuth
) : UserAuthenticationRepository {

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return try {
            authentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {}
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override suspend fun registerWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {}
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override suspend fun loginWithGoogleAccount(firebaseCredential: AuthCredential): Result<Unit> {
        return try {
            authentication.signInWithCredential(firebaseCredential)
                .addOnCompleteListener {}
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override suspend fun loginAnonymously(): Result<Unit> {
        return try {
            authentication.signInAnonymously()
                .addOnCompleteListener {}
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override fun logOut(): Result<Unit> {
        return try {
            if (authentication.currentUser != null) {
                authentication.signOut()
                Result.Success(Unit)
            } else {
                throw Exception("User is not logged")
            }
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override fun getUser(): FirebaseUser? {
        return authentication.currentUser
    }
}