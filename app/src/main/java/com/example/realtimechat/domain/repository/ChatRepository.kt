package com.example.realtimechat.domain.repository

import com.example.realtimechat.data.model.ChatThread
import com.example.realtimechat.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(message: Message)
    fun getMessagesBetween(user1: String, user2: String): Flow<List<Message>>
    fun getChatThreads(userId: String): Flow<List<ChatThread>>
}
