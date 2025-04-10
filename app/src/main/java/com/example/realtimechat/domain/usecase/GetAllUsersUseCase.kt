package com.example.realtimechat.domain.usecase

import com.example.realtimechat.data.model.User
import com.example.realtimechat.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    data class Result(
        val currentUser: User?,
        val otherUsers: List<User>
    )

    operator fun invoke(): Flow<Result> = flow {
        val currentUser = repository.getCurrentUser()
        val currentUserId = currentUser?.uid ?: ""
        repository.getOtherUsers(currentUserId).collect { otherUsers ->
            emit(Result(currentUser, otherUsers))
        }
    }
}

