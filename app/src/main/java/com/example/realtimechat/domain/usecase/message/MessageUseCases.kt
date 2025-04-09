package com.example.realtimechat.domain.usecase.message

data class MessageUseCases(
    val getMessages: GetMessagesUseCase,
    val sendMessage: SendMessageUseCase
)
