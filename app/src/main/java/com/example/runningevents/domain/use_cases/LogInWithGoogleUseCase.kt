package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.example.runningevents.utils.Result
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class LogInWithGoogleUseCase @Inject constructor(
    private val repository: UserAuthenticationRepository
) {
    suspend fun execute(firebaseCredential: AuthCredential): Result<Unit> {
        return repository.loginWithGoogleAccount(firebaseCredential = firebaseCredential)
    }
}