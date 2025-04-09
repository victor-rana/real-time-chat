package com.example.realtimechat.domain.usecase

import com.example.realtimechat.data.model.User
import com.example.realtimechat.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetAllUsersUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(currentUserId: String): Flow<List<User>> {
        return repository.getOtherUsers(currentUserId)
    }
}
