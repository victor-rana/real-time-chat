package com.example.realtimechat.presentation

import com.example.realtimechat.MainCoroutineRule
import com.example.realtimechat.data.model.Message
import com.example.realtimechat.domain.usecase.ChatStatusUseCase
import com.example.realtimechat.domain.usecase.message.GetMessagesUseCase
import com.example.realtimechat.domain.usecase.message.SendMessageUseCase
import com.example.realtimechat.domain.usecase.message.UploadImageAndSendMessageUseCase
import com.example.realtimechat.presentation.viewmodel.ChatViewModel
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val getMessagesUseCase: GetMessagesUseCase = mock()
    private val sendMessageUseCase: SendMessageUseCase = mock()
    private val uploadImageUseCase: UploadImageAndSendMessageUseCase = mock()
    private val chatStatusUseCase: ChatStatusUseCase = mock()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        viewModel = ChatViewModel(
            getMessagesUseCase,
            sendMessageUseCase,
            uploadImageUseCase,
            chatStatusUseCase
        )
    }

    @Test
    fun `loadMessages emits messages from usecase`() = runTest {
        val messages = listOf(
            Message("1", "sender", "receiver", "Hello", 123456L.toString()),
            Message("2", "receiver", "sender", "Hi", 123457L.toString())
        )

        whenever(getMessagesUseCase("sender", "receiver")).thenReturn(flowOf(messages))

        viewModel.loadMessages("sender", "receiver")

        val result = viewModel.messages.first()
        Truth.assertThat(result).isEqualTo(messages)
    }
}
