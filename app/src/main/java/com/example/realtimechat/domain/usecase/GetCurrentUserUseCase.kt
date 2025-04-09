package com.example.realtimechat.domain.usecase

import com.example.realtimechat.data.model.User
import com.example.realtimechat.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): User? = repository.getCurrentUser()
}
