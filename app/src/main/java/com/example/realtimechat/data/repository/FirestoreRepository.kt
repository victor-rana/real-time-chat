package com.example.realtimechat.data.repository

import com.example.realtimechat.data.model.ChatThread
import com.example.realtimechat.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

    suspend fun sendMessage(threadId: String, message: Message) {
        db.collection("chat_threads")
            .document(threadId)
            .collection("messages")
            .add(message)
            .await()

        // Update thread metadata
        db.collection("chat_threads")
            .document(threadId)
            .update(
                mapOf(
                    "lastMessage" to message.content,
                    "timestamp" to message.timestamp
                )
            ).await()
    }

    suspend fun createThreadIfNotExists(thread: ChatThread) {
        val docRef = db.collection("chat_threads").document(thread.threadId)
        val doc = docRef.get().await()
        if (!doc.exists()) {
            docRef.set(thread).await()
        }
    }
}
