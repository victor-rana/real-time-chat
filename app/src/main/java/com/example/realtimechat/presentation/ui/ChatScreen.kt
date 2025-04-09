package com.example.realtimechat.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.realtimechat.data.model.Message
import com.example.realtimechat.data.model.MessageType
import com.example.realtimechat.presentation.viewmodel.ChatViewModel
import com.example.realtimechat.utils.formatTimestamp
import com.example.realtimechat.utils.getBytesFromUri
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    currentUserId: String,
    otherUserId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isOtherTyping by viewModel.isOtherUserTyping.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val chatId = remember(currentUserId, otherUserId) {
        if (currentUserId < otherUserId) "${currentUserId}_$otherUserId"
        else "${otherUserId}_$currentUserId"
    }

    var messageText by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val bytes = getBytesFromUri(context, uri)
                if (bytes != null) {
                    viewModel.uploadImage(
                        imageBytes = bytes,
                        senderId = currentUserId,
                        receiverId = otherUserId
                    )
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.loadMessages(currentUserId, otherUserId)
        viewModel.observeTyping(chatId, otherUserId)
    }

    LaunchedEffect(messages.size) {
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                reverseLayout = true,
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(messages.size) { index ->
                    val msg = messages[messages.size - 1 - index]
                    ChatMessageBubble(
                        message = msg,
                        isSentByMe = msg.senderId == currentUserId
                    )
                }
            }

            if (isOtherTyping) {
                TypingIndicator()
            }

            MessageInputField(
                messageText = messageText,
                onTextChange = {
                    messageText = it
                    viewModel.onTyping(chatId, currentUserId)
                },
                onSend = {
                    if (messageText.isNotBlank()) {
                        val message = Message(
                            senderId = currentUserId,
                            receiverId = otherUserId,
                            content = messageText,
                            timestamp = System.currentTimeMillis(),
                            type = MessageType.TEXT
                        )
                        viewModel.sendMessage(message)
                        messageText = ""
                    }
                },
                onImageClick = { imagePickerLauncher.launch("image/*") }
            )
        }
    }
}



@Composable
fun ChatMessageBubble(message: Message, isSentByMe: Boolean) {
    val bubbleColor = if (isSentByMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSentByMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val alignment = if (isSentByMe) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(text = message.content, color = textColor)
                    }
                    MessageType.IMAGE -> {
                        AsyncImage(
                            model = message.content,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun MessageInputField(
    messageText: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onImageClick: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = onImageClick) {
                Icon(Icons.Default.Image, contentDescription = "Attach", tint = MaterialTheme.colorScheme.primary)
            }

            BasicTextField(
                value = messageText,
                onValueChange = onTextChange,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            Button(onClick = onSend, shape = RoundedCornerShape(12.dp)) {
                Text("Send")
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Text(
        text = "Typing...",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .padding(start = 16.dp, bottom = 4.dp)
            .alpha(0.7f)
    )
}




