package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class LogInWithEmailAndPasswordUseCase @Inject constructor(
    private val repository: UserAuthenticationRepository
) {
    suspend fun execute(email: String, password: String): Result<Unit> {
        return repository.loginWithEmailAndPassword(email = email, password = password)
    }
}