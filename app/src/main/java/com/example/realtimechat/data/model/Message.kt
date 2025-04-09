package com.example.realtimechat.data.model

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val status: MessageStatus = MessageStatus.SENT,
    val type: MessageType = MessageType.TEXT
)


enum class MessageStatus {
    SENT, DELIVERED
}

enum class MessageType {
    TEXT, IMAGE
}
