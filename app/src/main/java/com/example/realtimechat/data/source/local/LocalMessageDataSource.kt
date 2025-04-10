package com.example.realtimechat.data.source.local

import com.example.realtimechat.data.source.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

interface LocalMessageDataSource {
    suspend fun saveMessages(messages: List<MessageEntity>)
    fun getMessages(chatId: String): Flow<List<MessageEntity>>
}
