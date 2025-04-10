package com.example.realtimechat.data.source.local

import com.example.realtimechat.data.source.local.dao.MessageDao
import com.example.realtimechat.data.source.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalMessageDataSourceImpl @Inject constructor(
    private val messageDao: MessageDao
) : LocalMessageDataSource {

    override suspend fun saveMessages(messages: List<MessageEntity>) {
        messageDao.insertMessages(messages)
    }

    override fun getMessages(chatId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessages(chatId)
    }
}
