package com.example.realtimechat.data.source.remote

import com.example.realtimechat.data.model.Conversation
import com.example.realtimechat.data.model.Message
import kotlinx.coroutines.flow.Flow

interface FirestoreMessageDataSource {
    fun getMessages(currentUserId: String, otherUserId: String): Flow<List<Message>>
    suspend fun sendMessage(message: Message)
    suspend fun markMessagesAsDelivered(currentUserId: String, otherUserId: String)
    suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean)
    fun observeTypingStatus(chatId: String, userId: String): Flow<Boolean>
    suspend fun uploadImageAndSendMessage(
        imageBytes: ByteArray,
        senderId: String,
        receiverId: String,
        onSuccess: (imageUrl: String) -> Unit,
        onFailure: (Exception) -> Unit
    )
    fun getRecentConversations(userId: String): Flow<List<Conversation>>
}
