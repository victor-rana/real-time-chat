package com.example.realtimechat.data.model

data class ChatThread(
    val threadId: String = "",
    val userId: String = "",         // The other participant's user ID
    val userName: String = "",       // Display name
    val lastMessage: String = "",
    val timestamp: Long = 0L
)

