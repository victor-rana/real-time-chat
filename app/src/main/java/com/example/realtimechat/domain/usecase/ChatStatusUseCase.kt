package com.example.realtimechat.domain.usecase

import com.example.realtimechat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatStatusUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend fun markMessagesAsDelivered(currentUserId: String, otherUserId: String) {
        repository.markMessagesAsDelivered(currentUserId, otherUserId)
    }

    suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        repository.setTypingStatus(chatId, userId, isTyping)
    }

    fun observeTypingStatus(chatId: String, userId: String): Flow<Boolean> {
        return repository.observeTypingStatus(chatId, userId)
    }
}
