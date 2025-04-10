package com.example.realtimechat.domain.usecase.message

import com.example.realtimechat.data.model.Conversation
import com.example.realtimechat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentConversationsUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(userId: String): Flow<List<Conversation>> {
        return repository.getRecentConversations(userId)
    }
}
