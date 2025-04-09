package com.example.realtimechat.domain.usecase

import com.example.realtimechat.data.model.ChatThread
import com.example.realtimechat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatThreadsUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(currentUserId: String): Flow<List<ChatThread>> {
        return repository.getChatThreads(currentUserId)
    }
}
