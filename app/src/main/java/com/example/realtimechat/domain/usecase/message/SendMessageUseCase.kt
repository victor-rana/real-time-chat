package com.example.realtimechat.domain.usecase.message

import com.example.realtimechat.data.model.Message
import com.example.realtimechat.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(message: Message) {
        repository.sendMessage(message)
    }
}
