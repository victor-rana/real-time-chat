package com.example.realtimechat.domain.usecase.message

import com.example.realtimechat.data.model.Conversation
import com.example.realtimechat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRecentConversationsUseCase @Inject constructor(
    private val repository: MessageRepository
) {

    // Use 'collect' or 'first' to get the first emitted value from the Flow
    suspend operator fun invoke(userId: String): List<Conversation> {
        // Collecting the first emitted value from the Flow and returning it
        return repository.getRecentConversations(userId).first()
    }
}
