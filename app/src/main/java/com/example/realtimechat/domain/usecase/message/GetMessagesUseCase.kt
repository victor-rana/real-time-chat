package com.example.realtimechat.domain.usecase.message

import com.example.realtimechat.data.model.Message
import com.example.realtimechat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(currentUserId: String, otherUserId: String): Flow<List<Message>> {
        return repository.getMessages(currentUserId, otherUserId)
    }
}
