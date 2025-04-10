package com.example.realtimechat.data.source.remote

import com.example.realtimechat.data.mapper.toEntity
import com.example.realtimechat.data.model.Conversation
import com.example.realtimechat.data.model.Message
import com.example.realtimechat.data.model.MessageStatus
import com.example.realtimechat.data.model.MessageType
import com.example.realtimechat.data.source.local.LocalMessageDataSourceImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirestoreMessageDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val localDataSource: LocalMessageDataSourceImpl
) : FirestoreMessageDataSource {

    override fun getMessages(currentUserId: String, otherUserId: String): Flow<List<Message>> = callbackFlow {
        val chatId = generateChatId(currentUserId, otherUserId)

        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }

                // Save to local DB
                CoroutineScope(Dispatchers.IO).launch {
                    localDataSource.saveMessages(messages.map { it.toEntity(chatId) })
                }

                trySend(messages)
            }

        awaitClose { listener.remove() }
    }


    override suspend fun sendMessage(message: Message) {
        val chatId = generateChatId(message.senderId, message.receiverId)

        // Send the message
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.messageId)
            .set(message)
            .await()

        // Update the last_message field in the chat document
        val lastMessage = mapOf("content" to message.content, "timestamp" to message.timestamp)

        firestore.collection("chats")
            .document(chatId)
            .update("last_message", lastMessage)
            .await()
    }



    override suspend fun markMessagesAsDelivered(currentUserId: String, otherUserId: String) {
        val chatId = generateChatId(currentUserId, otherUserId)

        val snapshot = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .whereEqualTo("receiverId", currentUserId)
            .whereEqualTo("status", MessageStatus.SENT.name)
            .get()
            .await()

        snapshot.documents.forEach { doc ->
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(doc.id)
                .update("status", MessageStatus.DELIVERED.name)
                .await()
        }
    }

    override suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        firestore.collection("chats")
            .document(chatId)
            .collection("typing")
            .document(userId)
            .set(mapOf("isTyping" to isTyping))
            .await()
    }

    override fun observeTypingStatus(chatId: String, userId: String): Flow<Boolean> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("typing")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                val isTyping = snapshot?.getBoolean("isTyping") ?: false
                trySend(isTyping)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun uploadImageAndSendMessage(
        imageBytes: ByteArray,
        senderId: String,
        receiverId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val ref = storage.reference.child("chat_images/$fileName")
            val uploadTask = ref.putBytes(imageBytes).await()
            val imageUrl = ref.downloadUrl.await().toString()

            val chatId = generateChatId(senderId, receiverId)
            val messageId = generateMessageId(chatId)

            val message = Message(
                messageId = messageId,
                senderId = senderId,
                receiverId = receiverId,
                content = imageUrl, // image URL in content
                timestamp = System.currentTimeMillis(),
                type = MessageType.IMAGE
            )

            sendMessage(message)
            onSuccess(imageUrl)

        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_$user2" else "${user2}_$user1"
    }

    private fun generateMessageId(chatId: String): String {
        return firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()
            .id
    }

    override fun getRecentConversations(userId: String): Flow<List<Conversation>> = callbackFlow {
        val listener = firestore.collection("chats")
            .orderBy("last_message.timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val conversations = snapshot?.documents?.mapNotNull { doc ->
                    val lastMessageMap = doc.get("last_message") as? Map<*, *> ?: return@mapNotNull null
                    val senderId = doc.id.split("_").firstOrNull()
                    val receiverId = doc.id.split("_").lastOrNull()
                    if (senderId == null || receiverId == null) return@mapNotNull null
                    if (senderId != userId && receiverId != userId) return@mapNotNull null

                    Conversation(
                        chatId = doc.id,
                        lastMessage = Message(
                            messageId = "",
                            senderId = senderId,
                            receiverId = receiverId,
                            content = lastMessageMap["content"] as? String ?: "",
                            timestamp = (lastMessageMap["timestamp"] as? Number)?.toLong() ?: 0L
                        ),
                        participants = listOf(senderId, receiverId)
                    )
                } ?: emptyList()

                trySend(conversations)
            }

        awaitClose { listener.remove() }
    }

}
