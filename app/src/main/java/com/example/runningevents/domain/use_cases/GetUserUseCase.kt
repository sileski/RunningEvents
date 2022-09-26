package com.example.runningevents.domain.use_cases

import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: UserAuthenticationRepository
) {
    fun execute(): FirebaseUser? {
        return repository.getUser()
    }
}