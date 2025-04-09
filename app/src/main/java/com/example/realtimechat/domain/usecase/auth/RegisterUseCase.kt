package com.example.realtimechat.domain.usecase.auth

import com.example.realtimechat.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit> {
        return authRepository.register(name, email, password)
    }
}