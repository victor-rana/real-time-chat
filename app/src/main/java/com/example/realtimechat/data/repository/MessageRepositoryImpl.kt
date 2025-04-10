package com.example.realtimechat.data.repository

import com.example.realtimechat.data.mapper.toEntity
import com.example.realtimechat.data.mapper.toMessage
import com.example.realtimechat.data.model.Conversation
import com.example.realtimechat.data.model.Message
import com.example.realtimechat.data.model.MessageType
import com.example.realtimechat.data.source.local.LocalMessageDataSource
import com.example.realtimechat.data.source.remote.FirestoreMessageDataSource
import com.example.realtimechat.domain.repository.MessageRepository
import com.example.realtimechat.utils.generateChatId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirestoreMessageDataSource,
    private val localDataSource: LocalMessageDataSource
) : MessageRepository {

    override fun getMessages(currentUserId: String, otherUserId: String): Flow<List<Message>> = flow {
        val chatId = generateChatId(currentUserId, otherUserId)

        // Emit local messages first (cached ones)
        localDataSource.getMessages(chatId).collect { localMessages ->
            emit(localMessages.map { it.toMessage() })
        }

        // Fetch latest from remote and cache them
        remoteDataSource.getMessages(currentUserId, otherUserId).collect { remoteMessages ->
            localDataSource.saveMessages(remoteMessages.map { it.toEntity(chatId) })
            emit(remoteMessages) // emit fresh ones
        }
    }



    override suspend fun sendMessage(message: Message) {
        remoteDataSource.sendMessage(message)
    }

    override suspend fun markMessagesAsDelivered(currentUserId: String, otherUserId: String) =
        remoteDataSource.markMessagesAsDelivered(currentUserId, otherUserId)

    override suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        remoteDataSource.setTypingStatus(chatId, userId, isTyping)
    }

    override fun observeTypingStatus(chatId: String, userId: String): Flow<Boolean> {
        return remoteDataSource.observeTypingStatus(chatId, userId)
    }

    override suspend fun uploadImageAndSendMessage(
        imageBytes: ByteArray,
        senderId: String,
        receiverId: String,
        onSuccess: (imageUrl: String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val imageId = UUID.randomUUID().toString()
            val imageRef = FirebaseStorage.getInstance()
                .reference
                .child("chat_images/$imageId.jpg")

            imageRef.putBytes(imageBytes).await()
            val imageUrl = imageRef.downloadUrl.await().toString()

            val message = Message(
                messageId = imageId,
                senderId = senderId,
                receiverId = receiverId,
                content = imageUrl,
                timestamp = System.currentTimeMillis(),
                type = MessageType.IMAGE
            )

            val chatId = if (senderId < receiverId) "${senderId}_${receiverId}" else "${receiverId}_${senderId}"

            FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .document(message.messageId)
                .set(message)
                .await()

            onSuccess(imageUrl)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun getRecentConversations(userId: String): Flow<List<Conversation>> {
        return remoteDataSource.getRecentConversations(userId)
    }

}