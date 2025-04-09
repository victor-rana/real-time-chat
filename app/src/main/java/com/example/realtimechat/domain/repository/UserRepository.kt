package com.example.realtimechat.domain.repository

import com.example.realtimechat.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getOtherUsers(currentUserId: String): Flow<List<User>>
    suspend fun getCurrentUser(): User?
}

