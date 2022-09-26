package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class LogInAnonymouslyUseCase @Inject constructor(
    private val repository: UserAuthenticationRepository
) {
    suspend fun execute(): Result<Unit> {
        return repository.loginAnonymously()
    }
}