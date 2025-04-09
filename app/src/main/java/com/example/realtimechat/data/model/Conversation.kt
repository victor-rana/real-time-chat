package com.example.realtimechat.data.model

data class Conversation(
    val chatId: String = "",
    val lastMessage: Message = Message(),
    val participants: List<String> = emptyList()
)

