package com.example.ecommerceapp.domain.usecase.auth

import com.example.ecommerceapp.domain.model.User
import com.example.ecommerceapp.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> = repository.signInWithGoogle(idToken)
}
