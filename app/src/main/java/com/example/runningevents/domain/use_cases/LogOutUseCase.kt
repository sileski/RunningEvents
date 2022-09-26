package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.example.runningevents.utils.Result
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val repository: UserAuthenticationRepository
) {
    fun execute(): Result<Unit> {
        return repository.logOut()
    }
}