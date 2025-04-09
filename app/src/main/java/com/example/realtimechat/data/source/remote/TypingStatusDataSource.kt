package com.example.realtimechat.data.source.remote

import kotlinx.coroutines.flow.Flow

interface TypingStatusDataSource {
    fun observeTypingStatus(chatId: String, userId: String): Flow<Boolean>
    suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean)
}
