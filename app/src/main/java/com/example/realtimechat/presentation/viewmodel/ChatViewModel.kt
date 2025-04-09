package com.example.realtimechat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realtimechat.data.model.Conversation
import com.example.realtimechat.data.model.Message
import com.example.realtimechat.data.model.MessageStatus
import com.example.realtimechat.data.model.MessageType
import com.example.realtimechat.domain.usecase.*
import com.example.realtimechat.domain.usecase.message.GetMessagesUseCase
import com.example.realtimechat.domain.usecase.message.GetRecentConversationsUseCase
import com.example.realtimechat.domain.usecase.message.SendMessageUseCase
import com.example.realtimechat.domain.usecase.message.UploadImageAndSendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val uploadImageMessageUseCase: UploadImageAndSendMessageUseCase,
    private val chatStatusUseCase: ChatStatusUseCase,
    private val getRecentConversationsUseCase: GetRecentConversationsUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isOtherUserTyping = MutableStateFlow(false)
    val isOtherUserTyping: StateFlow<Boolean> = _isOtherUserTyping

    // StateFlow to hold the conversations list
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadMessages(currentUserId: String, otherUserId: String) {
        viewModelScope.launch {
            getMessagesUseCase(currentUserId, otherUserId)
                .collectLatest { messageList ->
                    _messages.value = messageList

                    val hasUndelivered = messageList.any {
                        it.receiverId == currentUserId && it.status == MessageStatus.SENT
                    }

                    if (hasUndelivered) {
                        chatStatusUseCase.markMessagesAsDelivered(currentUserId, otherUserId)
                    }
                }
        }
    }

//    fun getRecentConversations(userId: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val result = getRecentConversationsUseCase(userId)
//                _conversations.value = result
//            } catch (e: Exception) {
//                _errorMessage.value = "Failed to load conversations: ${e.message}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            sendMessageUseCase(message)
        }
    }

    fun onTyping(chatId: String, userId: String) {
        viewModelScope.launch {
            chatStatusUseCase.setTypingStatus(chatId, userId, true)
        }
    }

    fun observeTyping(chatId: String, otherUserId: String) {
        viewModelScope.launch {
            chatStatusUseCase.observeTypingStatus(chatId, otherUserId)
                .collectLatest {
                    _isOtherUserTyping.value = it
                }
        }
    }

    fun uploadImage(
        imageBytes: ByteArray,
        senderId: String,
        receiverId: String
    ) {
        viewModelScope.launch {
            uploadImageMessageUseCase(
                imageBytes,
                senderId,
                receiverId,
                onSuccess = { imageUrl ->
                    val message = Message(
                        messageId = System.currentTimeMillis().toString(),
                        senderId = senderId,
                        receiverId = receiverId,
                        content = imageUrl,
                        timestamp = System.currentTimeMillis(),
                        type = MessageType.IMAGE
                    )
                    sendMessage(message)
                },
                onFailure = {
                    // handle error if needed
                }
            )
        }
    }
}
