package com.example.realtimechat.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TypingStatusDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TypingStatusDataSource {

    override fun observeTypingStatus(chatId: String, userId: String): Flow<Boolean> = callbackFlow {
        val docId = "${chatId}_$userId"
        val docRef = firestore.collection("typingStatus").document(docId)

        val listener = docRef.addSnapshotListener { snapshot, _ ->
            val isTyping = snapshot?.getBoolean("isTyping") ?: false
            trySend(isTyping)
        }

        awaitClose { listener.remove() }
    }

    override suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        val docId = "${chatId}_$userId"
        firestore.collection("typingStatus").document(docId)
            .set(mapOf("isTyping" to isTyping))
            .await()
    }
}
