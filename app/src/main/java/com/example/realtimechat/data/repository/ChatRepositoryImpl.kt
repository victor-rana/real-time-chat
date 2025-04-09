package com.example.realtimechat.data.repository

import com.example.realtimechat.data.model.ChatThread
import com.example.realtimechat.data.model.Message
import com.example.realtimechat.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override suspend fun sendMessage(message: Message) {
        val threadId = getThreadId(message.senderId, message.receiverId)

        val messageRef = firestore.collection("messages")
            .document(threadId)
            .collection("chats")
            .document()

        val messageWithId = message.copy(messageId = messageRef.id)
        messageRef.set(messageWithId).await()

        // Update chat thread info for both users
        val threadData = mapOf(
            "threadId" to threadId,
            "userId" to message.receiverId,
            "lastMessage" to message.content,
            "timestamp" to message.timestamp
        )

        firestore.collection("threads")
            .document(message.senderId)
            .collection("userThreads")
            .document(message.receiverId)
            .set(threadData).await()

        val reverseThreadData = threadData.toMutableMap().apply {
            this["userId"] = message.senderId
        }

        firestore.collection("threads")
            .document(message.receiverId)
            .collection("userThreads")
            .document(message.senderId)
            .set(reverseThreadData).await()
    }

    override fun getMessagesBetween(user1: String, user2: String) = callbackFlow {
        val threadId = getThreadId(user1, user2)

        val listener = firestore.collection("messages")
            .document(threadId)
            .collection("chats")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val messages = snapshot.documents.mapNotNull {
                    it.toObject(Message::class.java)
                }
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    override fun getChatThreads(userId: String) = callbackFlow {
        val listener = firestore.collection("threads")
            .document(userId)
            .collection("userThreads")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val threads = snapshot.documents.mapNotNull {
                    it.toObject(ChatThread::class.java)
                }
                trySend(threads)
            }

        awaitClose { listener.remove() }
    }

    private fun getThreadId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_$user2" else "${user2}_$user1"
    }
}
