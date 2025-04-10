package com.example.realtimechat.presentation.ui.chatlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.realtimechat.data.model.User
import com.example.realtimechat.presentation.viewmodel.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onChatClick: (User) -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val otherUsers by viewModel.otherUsers.collectAsState()
    val recentConversations by viewModel.recentConversations.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Instead of directly accessing viewModel.otherUsers.value inside items {}
    // use the collected value we already have above
    Scaffold(
        topBar = { TopAppBar(title = { Text("Chats") }) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (recentConversations.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Recent Conversations",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            items(recentConversations) { conversation ->
                                val otherParticipantId = conversation.participants.firstOrNull { it != currentUser?.uid }
                                val user = otherUsers.find { it.uid == otherParticipantId }
                                user?.let {
                                    ConversationItem(user = it, lastMessage = conversation.lastMessage.content) {
                                        onChatClick(it)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "New Chat",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        items(otherUsers) { user ->
                            ChatUserItem(user = user) {
                                onChatClick(user)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ChatUserItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user.profileImageUrl)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = user.name, fontWeight = FontWeight.Bold)
            Text(text = user.email, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ConversationItem(user: User, lastMessage: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user.profileImageUrl)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = user.name, fontWeight = FontWeight.Bold)
            Text(
                text = lastMessage,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun UserAvatar(url: String?) {
    if (!url.isNullOrEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(model = url),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    } else {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("U", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
