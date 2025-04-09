package com.example.realtimechat.data.repository

import com.example.realtimechat.data.model.User
import com.example.realtimechat.data.source.FirestoreUserDataSource
import com.example.realtimechat.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userRemote: FirestoreUserDataSource
) : UserRepository {

    override fun getOtherUsers(currentUserId: String): Flow<List<User>> =
        userRemote.getAllUsersExceptCurrent(currentUserId)

    override suspend fun getCurrentUser(): User? =
        userRemote.getCurrentUser()
}
