package com.example.realtimechat.domain.usecase.message

import com.example.realtimechat.domain.repository.MessageRepository
import javax.inject.Inject

class UploadImageAndSendMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        senderId: String,
        receiverId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.uploadImageAndSendMessage(
            imageBytes = imageBytes,
            senderId = senderId,
            receiverId = receiverId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}

