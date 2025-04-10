package com.example.realtimechat.data.mapper

import com.example.realtimechat.data.model.Message
import com.example.realtimechat.data.model.MessageStatus
import com.example.realtimechat.data.model.MessageType
import com.example.realtimechat.data.source.local.entity.MessageEntity

fun MessageEntity.toMessage(): Message {
    return Message(
        messageId = messageId,
        chatId = chatId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        timestamp = timestamp,
        type = MessageType.TEXT,
        status = MessageStatus.SENT
    )
}

fun Message.toEntity(chatId: String): MessageEntity {
    return MessageEntity(
        messageId = messageId,
        chatId = chatId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        timestamp = timestamp,
        type = type.name,
        status = status.name
    )
}

